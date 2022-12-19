package ru.yandex.practicum.filmorate.exception;

public class FilmNotFoundException extends RuntimeException {
    private final Integer filmId;

    public FilmNotFoundException(Integer filmId) {
        this.filmId = filmId;
    }

    public Integer getFilmId() {
        return filmId;
    }
}