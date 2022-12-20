package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    private final UserStorage userStorage;
    private final ru.yandex.practicum.filmorate.service.UserService userService;

    @Autowired
    public UserController(InMemoryUserStorage userStorage, ru.yandex.practicum.filmorate.service.UserService userService) {
        this.userStorage = userStorage;
        this.userService = userService;
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

    @GetMapping("/{id}")
    public User getUser(@PathVariable("id") Integer id) {
        User user = userService.getUser(id);
        if (user == null) {
            throw new EntityNotFoundException(id, Entity.USER);
        }
        return user;
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.addFriend(userId, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void removeFriend(@PathVariable("id") Integer userId, @PathVariable("friendId") Integer friendId) {
        userService.removeFriend(userId, friendId);
    }

    @GetMapping("{id}/friends")
    public List<User> getFriends(@PathVariable("id") Integer userId) {
        return userService.getFriends(userId);
    }

    @GetMapping("{id}/friends/common/{otherId}")
    public Set<User> getCommonFriends(@PathVariable("id") Integer userId, @PathVariable("otherId") Integer friendId) {
        return userService.getCommonFriends(userId, friendId);
    }

    private static void handleSpringValidation(Errors errors) {
        User.decrementIdCounter();
        throw new ValidationException("Произошла ошибка - " + errors.getAllErrors());
    }

}
