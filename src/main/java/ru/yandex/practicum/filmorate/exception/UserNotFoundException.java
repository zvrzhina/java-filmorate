package ru.yandex.practicum.filmorate.exception;

public class UserNotFoundException extends RuntimeException {
    private final Integer userId;

    public UserNotFoundException(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }
}
