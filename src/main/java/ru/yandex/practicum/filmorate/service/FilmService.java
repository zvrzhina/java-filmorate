package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class FilmService {
    private final FilmStorage dbFilm;
    private final UserService userService;

    @Autowired
    public FilmService(FilmStorage dbFilm, UserService userService) {
        this.dbFilm = dbFilm;
        this.userService = userService;
    }

    public Film getFilm(Integer id) {
        return dbFilm.getAll()
                .stream()
                .filter(film -> id.equals(film.getId()))
                .findAny()
                .orElse(null);
    }

    public List<Film> getAll() {
        return dbFilm.getAll();
    }

    public Film create(Film film) {
        return dbFilm.create(film);
    }

    public Film update(Film film) {
        if (getFilm(film.getId()) == null) {
            throw new EntityNotFoundException(film.getId(), Entity.FILM);
        }
        return dbFilm.update(film);
    }

    public void setLike(Integer filmId, Integer userId) {
        User user = userService.getUser(userId);
        Film film = getFilm(filmId);
        if (user == null) {
            throw new EntityNotFoundException(userId, Entity.USER);
        } else if (film == null) {
            throw new EntityNotFoundException(filmId, Entity.FILM);
        } else {
            film.addLike(userId);
            dbFilm.update(film); // записать в базу лайк
            log.info("Пользователь с id = " + userId + " поставил лайк фильму с id = " + filmId);
        }
    }

    public void deleteLike(Integer filmId, Integer userId) {
        User user = userService.getUser(userId);
        Film film = getFilm(filmId);
        if (user == null) {
            throw new EntityNotFoundException(userId, Entity.USER);
        } else if (film == null) {
            throw new EntityNotFoundException(filmId, Entity.FILM);
        } else {
            if (film.getLikes().contains(userId)) {
                film.removeLike(userId);
                dbFilm.update(film); // удалить лайк из базы
                log.info("Пользователь с id = " + userId + " удалил лайк с фильма с id = " + filmId);
            } else {
                throw new EntityNotFoundException(userId, Entity.LIKE);
            }
        }
    }

    public List<Film> getPopular(Integer count) {
        if (count == null) {
            count = 10; // set default as 10
        }
        List<Film> result = dbFilm.getAll()
                .stream()
                .sorted(Comparator.comparing(Film::getLikesAmount).reversed())
                .limit(count)
                .collect(Collectors.toList());
        log.info("Список из " + count + " самых популярных фильмов: \n" + result);
        return result;
    }
}
