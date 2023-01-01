package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/films")
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    public List<Film> getAll() {
        log.info("Текущее количество фильмов: {}", filmService.getAll().size());
        return new ArrayList<>(filmService.getAll());
    }

    @PostMapping
    public Film create(@Valid @RequestBody Film film, Errors errors) {
        if (errors.hasErrors()) {
            handleSpringValidation(errors);
        }
        return filmService.create(film);
    }

    @PutMapping
    public Film update(@Valid @RequestBody Film film, Errors errors) {
        if (errors.hasErrors()) {
            handleSpringValidation(errors);
        }
        return filmService.update(film);
    }

    @GetMapping("/{id}")
    public Film getFilm(@PathVariable("id") Integer id) {
        Film film = filmService.getFilm(id);
        if (film == null) {
            throw new EntityNotFoundException(id, Entity.FILM);
        }
        return filmService.getFilm(id);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.setLike(filmId, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable("id") Integer filmId, @PathVariable("userId") Integer userId) {
        filmService.deleteLike(filmId, userId);
    }

    @GetMapping(value = {"/popular", "/popular?count={count}"})
    public List<Film> getPopular(@RequestParam(name = "count", required = false) Integer count) {
        return filmService.getPopular(count);
    }

    private static void handleSpringValidation(Errors errors) {
        Film.decrementIdCounter();
        throw new ValidationException("Произошла ошибка - " + errors.getAllErrors());
    }
}
