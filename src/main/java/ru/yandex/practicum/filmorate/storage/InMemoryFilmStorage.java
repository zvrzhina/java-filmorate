package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class InMemoryFilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    public Collection<Film> findAll() {
        return films.values();
    }

    public Film create(Film film) {
        validate(film);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    public Film update(Film film) {
        for (Film f : films.values()) {
            if (f.getId() == film.getId()) {
                validate(film);
                films.put(film.getId(), film);
                log.info("Обновлен фильм: {}", film);
                return film;
            }
        }
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Фильм с id " + film.getId() + " не найден");
    }

    private static void validate(Film film) {
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
