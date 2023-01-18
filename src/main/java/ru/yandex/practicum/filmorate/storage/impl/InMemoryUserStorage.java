package ru.yandex.practicum.filmorate.storage.impl;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@NoArgsConstructor
@Component("inMemoryUser")
public class InMemoryUserStorage extends UserStorageImpl implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    public User create(User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя установлено как логин: {}", user.getName());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    public User update(User user) {
        for (User u : users.values()) {
            if (u.getId() == user.getId()) {
                validate(user);
                if (user.getName() == null || user.getName().isBlank()) {
                    user.setName(user.getLogin());
                    log.info("Имя установлено как логин: {}", user.getName());
                }
                users.put(user.getId(), user);
                log.info("Обновлен пользователь: {}", user);
                return user;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Пользователь с id " + user.getId() + " не найден");
    }

}
