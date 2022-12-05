package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> getAll() {
        log.info("Текущее количество фильмов: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film) {
        validate(film);
        films.put(film.getId(), film);
        log.info("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film) {
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
