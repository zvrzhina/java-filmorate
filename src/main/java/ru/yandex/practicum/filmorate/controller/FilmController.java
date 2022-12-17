package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {

    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(FilmStorage filmStorage) {
        this.filmStorage = filmStorage;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Текущее количество фильмов: {}", filmStorage.getAll().size());
        return new ArrayList<>(filmStorage.getAll());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film, Errors errors) {
        if (errors.hasErrors()) {
            handleSpringValidation(errors);
        }
        return filmStorage.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film, Errors errors) {
        if (errors.hasErrors()) {
            handleSpringValidation(errors);
        }
        return filmStorage.update(film);
    }

    private static void handleSpringValidation(Errors errors) {
        Film.decrementIdCounter();
        throw new ValidationException("Произошла ошибка - " + errors.getAllErrors());
    }
}
