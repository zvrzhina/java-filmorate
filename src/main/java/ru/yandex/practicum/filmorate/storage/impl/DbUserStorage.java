package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Component("dbUser")
public class DbUserStorage extends UserStorageImpl implements UserStorage {
    private final Logger log = LoggerFactory.getLogger(DbFilmStorage.class);

    private final JdbcTemplate jdbcTemplate;

    public DbUserStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> getAll() {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS");

        List<User> users = new ArrayList<>();

        if (userRows.next() == false) {
            log.info("Список пользователей пуст.");
            return Collections.emptyList();
        } else {
            do {
                SqlRowSet friendsSet = jdbcTemplate.queryForRowSet("SELECT FRIEND_ID, IS_ACCEPTED FROM FRIEND WHERE USER_ID = ?", userRows.getInt("USER_ID"));
                Map<Integer, Boolean> friends = new HashMap<>();
                while (friendsSet.next()) {
                    friends.put(friendsSet.getInt("FRIEND_ID"), friendsSet.getBoolean("IS_ACCEPTED"));
                }
                User user = new User(
                        userRows.getInt("USER_ID"),
                        userRows.getString("EMAIL"),
                        userRows.getString("LOGIN"),
                        userRows.getString("NAME"),
                        userRows.getDate("BIRTHDAY").toLocalDate(),
                        friends);

                log.info("Найден пользователь: {} {}", user.getId(), user.getName());
                users.add(user);
            }
            while (userRows.next());
        }
        return users;
    }

    @Override
    public User create(User user) {
        String userQuery = "INSERT INTO USERS(EMAIL, LOGIN, NAME, BIRTHDAY) " +
                "VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя установлено как логин: {}", user.getName());
        }
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(userQuery, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        int userId = keyHolder.getKey().intValue();
        user.setId(userId);
        updateFriends(user);
        return user;
    }

    @Override
    public User update(User user) {
        String userQuery = "UPDATE USERS SET " +
                "EMAIL = ?, LOGIN = ?, NAME = ?, BIRTHDAY = ? " +
                "WHERE USER_ID = ?";
        validate(user);
        jdbcTemplate.update(userQuery
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , user.getBirthday()
                , user.getId());
        updateFriends(user);
        return user;
    }

    private void updateFriends(User user) {
        String cleanupQuery = "DELETE FROM FRIEND WHERE USER_ID=?";
        jdbcTemplate.update(cleanupQuery,
                user.getId());
        String friendsQuery = "MERGE INTO FRIEND(USER_ID, FRIEND_ID, IS_ACCEPTED) KEY(USER_ID, FRIEND_ID) " +
                "VALUES (?, ?, ?)";
        if (user.getFriends() != null) {
            for (Map.Entry<Integer, Boolean> entry : user.getFriends().entrySet()) {
                jdbcTemplate.update(friendsQuery,
                        user.getId(),
                        entry.getKey(),
                        entry.getValue());
            }
        }
    }

}
