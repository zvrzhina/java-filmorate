package ru.yandex.practicum.filmorate.exception;

public class LikeNotFoundException extends RuntimeException {
    private final Integer userId;
    private final Integer filmId;

    public LikeNotFoundException(Integer userId, Integer filmId) {
        this.userId = userId;
        this.filmId = filmId;
    }

    public Integer getUserId() {
        return userId;
    }

    public Integer getFilmId() {
        return filmId;
    }
}