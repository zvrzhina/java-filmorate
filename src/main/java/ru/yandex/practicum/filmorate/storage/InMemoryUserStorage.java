package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage {
    private final Map<Integer, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
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

    private static void validate(User user) {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            User.decrementIdCounter();
            throw new ValidationException("Логин не может быть пустым или содержать пробелы");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            User.decrementIdCounter();
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }


}
