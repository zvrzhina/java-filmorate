package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;

    @Autowired
    public UserController(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    @GetMapping
    public List<User> getAll() {
        log.info("Текущее количество пользователей: {}", userStorage.getAll().size());
        return new ArrayList<>(userStorage.getAll());
    }

    @PostMapping
    public User create(@Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            handleSpringValidation(errors);
        }
        return userStorage.create(user);
    }

    @PutMapping
    public User update(@Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) {
            handleSpringValidation(errors);
        }
        return userStorage.update(user);
    }

    private static void handleSpringValidation(Errors errors) {
        User.decrementIdCounter();
        throw new ValidationException("Произошла ошибка - " + errors.getAllErrors());
    }

}
