package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserService {

    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User getUser(Integer id) {
        return userStorage.getAll()
                .stream()
                .filter(user -> id.equals(user.getId()))
                .findAny()
                .orElse(null);
    }

    public void addFriend(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user == null) {
            throw new EntityNotFoundException(userId, Entity.USER);
        } else if (friend == null) {
            throw new EntityNotFoundException(friendId, Entity.USER);
        } else {
            user.addFriend(friendId);
            friend.addFriend(userId);
        }
        log.info("Пользователь с id = " + userId + " добавил друга с id = " + friendId);
    }

    public void removeFriend(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user == null) {
            throw new EntityNotFoundException(userId, Entity.USER);
        } else if (friend == null) {
            throw new EntityNotFoundException(friendId, Entity.USER);
        } else {
            if (user.getFriends().contains(friendId)) {
                user.removeFriend(friendId);
                friend.removeFriend(userId);
                log.info("Пользователь с id = " + userId + " удалил друга с id = " + friendId);
            } else {
                throw new EntityNotFoundException(userId, Entity.USER);
            }
        }
    }

    public List<User> getFriends(Integer userId) {
        User user = getUser(userId);
        if (user == null) {
            throw new EntityNotFoundException(userId, Entity.USER);
        } else {
            List<User> friends = new ArrayList<>();
            for (Integer id : user.getFriends()) {
                friends.add(getUser(id));
            }
            log.info("Список друзей пользователя с id = " + userId + " получен");
            return friends;
        }
    }

    public Set<User> getCommonFriends(Integer userId, Integer friendId) {
        User user = getUser(userId);
        User friend = getUser(friendId);
        if (user == null) {
            throw new EntityNotFoundException(userId, Entity.USER);
        } else if (friend == null) {
            throw new EntityNotFoundException(friendId, Entity.USER);
        } else {
            List<User> firstUserFriends = getFriends(userId);
            List<User> secondUserFriends = getFriends(friendId);
            Set<User> result = firstUserFriends.stream()
                    .distinct()
                    .filter(secondUserFriends::contains)
                    .collect(Collectors.toSet());
            log.info("Список общих друзей пользователя с id = " + userId + " с пользователем с id = " + friendId + " получен");
            return result;
        }
    }
}
