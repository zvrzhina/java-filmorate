package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> getAll() {
        log.info("Текущее количество пользователей: {}", users.size());
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User create(@RequestBody User user) {
        validate(user);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
            log.info("Имя установлено как логин: {}", user.getName());
        }
        users.put(user.getId(), user);
        log.info("Добавлен пользователь: {}", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) {
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
        if (user.getEmail() == null || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            User.decrementIdCounter();
            throw new ValidationException("Некоректный адрес электронной почты. Проверьте введенное значение.");
        }
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
