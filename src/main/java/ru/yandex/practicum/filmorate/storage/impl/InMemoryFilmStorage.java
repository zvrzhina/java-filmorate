package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("inMemoryFilm")
public class InMemoryFilmStorage extends FilmStorageImpl implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();

    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film getFilm(Integer id) {
        if (!films.containsKey(id)) {
            throw new EntityNotFoundException(id, Entity.FILM);
        }
        return films.get(id);
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
}
