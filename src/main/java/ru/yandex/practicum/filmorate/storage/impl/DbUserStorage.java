package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component("dbUser")
public class DbUserStorage extends UserStorageImpl implements UserStorage {

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
                users.add(getUser(userRows.getInt("USER_ID")));
            }
            while (userRows.next());
        }
        return users;
    }


    public User getUser(Integer id) {
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM USERS where USER_ID = ?", id);
        if (userRows.next()) {
            SqlRowSet friendsSet = jdbcTemplate.queryForRowSet("SELECT FRIEND_ID, IS_ACCEPTED FROM FRIEND WHERE USER_ID = ?", id);
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
            return user;
        } else {
            throw new EntityNotFoundException(id, Entity.USER);
        }
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
        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
            List<Integer> friendIds = new ArrayList<>();
            List<Boolean> statuses = new ArrayList<>();
            for (Map.Entry<Integer, Boolean> entry : user.getFriends().entrySet()) {
                friendIds.add(entry.getKey());
                statuses.add(entry.getValue());
            }
            jdbcTemplate.update(connection -> {
                PreparedStatement smth = connection.prepareStatement(friendsQuery);
                for (int i = 0; i < friendIds.size(); i++) {
                    smth.setInt(1, user.getId());
                    smth.setInt(2, friendIds.get(i));
                    smth.setBoolean(3, statuses.get(i));
                    smth.addBatch();
                }
                smth.executeBatch();
                return smth;
            });
        }
    }

}
