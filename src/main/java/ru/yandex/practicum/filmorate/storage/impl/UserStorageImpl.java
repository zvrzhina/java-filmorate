package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

@Component
public class UserStorageImpl {
    protected static void validate(User user) {
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
