package ru.yandex.practicum.filmorate.storage.impl;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Component
public class FilmStorageImpl {
    public static void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            Film.decrementIdCounter();
            throw new ValidationException("Название фильма не может быть пустым.");
        }
        if (film.getDescription().length() > 200) {
            Film.decrementIdCounter();
            throw new ValidationException("Максимальная длина описания — 200 символов.");
        }
        if (film.getReleaseDate().isBefore(LocalDate.parse("1895-12-28"))) {
            Film.decrementIdCounter();
            throw new ValidationException("Дата релиза — не раньше 28 декабря 1895 года.");
        }
        if (film.getDuration() <= 0) {
            Film.decrementIdCounter();
            throw new ValidationException("Продолжительность фильма должна быть положительной.");
        }
    }
}
