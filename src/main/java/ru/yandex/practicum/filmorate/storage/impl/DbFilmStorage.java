package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.FilmStorage;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component("dbFilm")
public class DbFilmStorage extends FilmStorageImpl implements FilmStorage {

    private final JdbcTemplate jdbcTemplate;

    public DbFilmStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Film> getAll() {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM");

        List<Film> films = new ArrayList<>();

        if (filmRows.next() == false) {
            log.info("Список фильмов пуст.");
            return Collections.emptyList();
        } else {
            do {
                films.add(getFilm(filmRows.getInt("FILM_ID")));
            }
            while (filmRows.next());
        }
        return films;
    }

    @Override
    public Film getFilm(Integer id) {
        SqlRowSet filmRows = jdbcTemplate.queryForRowSet("SELECT * FROM FILM WHERE FILM_ID = ?", id);
        if (filmRows.next()) {
            SqlRowSet genresSet = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE WHERE GENRE_ID in (SELECT GENRE_ID FROM FILM_GENRE WHERE FILM_ID = ?)", filmRows.getInt("FILM_ID"));
            LinkedHashSet<Genre> genres = new LinkedHashSet<>();
            while (genresSet.next()) {
                Genre genre = new Genre(genresSet.getInt("GENRE_ID"), genresSet.getString("NAME"));
                genres.add(genre);
            }
            SqlRowSet mpaSet = jdbcTemplate.queryForRowSet("SELECT * FROM RATING WHERE RATING_ID = ?", filmRows.getInt("RATING_ID"));
            Mpa mpa;
            if (mpaSet.next()) {
                mpa = new Mpa(mpaSet.getInt("RATING_ID"), mpaSet.getString("Name"));
            } else {
                mpa = null;
            }
            SqlRowSet likesSet = jdbcTemplate.queryForRowSet("SELECT * FROM LIKES WHERE FILM_ID = ?", filmRows.getInt("FILM_ID"));
            Set<Integer> likes = new HashSet<>();
            while (likesSet.next()) {
                likes.add(likesSet.getInt("USER_ID"));
            }
            Film film = new Film(
                    filmRows.getInt("FILM_ID"),
                    filmRows.getString("NAME"),
                    filmRows.getString("DESCRIPTION"),
                    filmRows.getDate("RELEASE_DATE").toLocalDate(),
                    filmRows.getInt("DURATION"),
                    mpa,
                    genres,
                    likes);
            log.info("Найден фильм: {} {}", film.getId(), film.getName());
            return film;
        } else {
            throw new EntityNotFoundException(id, Entity.FILM);
        }
    }

    @Override
    public Film create(Film film) {
        String filmQuery = "INSERT INTO FILM(NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATING_ID) " +
                "VALUES (?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        validate(film);
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(filmQuery, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setLong(4, film.getDuration());
            stmt.setInt(5, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        int filmId = keyHolder.getKey().intValue();
        film.setId(filmId);
        insertGenres(film);
        return film;
    }

    @Override
    public Film update(Film film) {
        String filmQuery = "UPDATE FILM SET " +
                "NAME = ?, DESCRIPTION = ?, RELEASE_DATE = ?, DURATION = ?, RATING_ID = ? " +
                "WHERE FILM_ID = ?";
        validate(film);
        jdbcTemplate.update(filmQuery
                , film.getName()
                , film.getDescription()
                , film.getReleaseDate()
                , film.getDuration()
                , film.getMpa().getId()
                , film.getId());
        String deleteGenreQuery = "DELETE FROM FILM_GENRE WHERE FILM_ID = ?";
        jdbcTemplate.update(deleteGenreQuery, film.getId());
        insertGenres(film);
        insertLikes(film);
        return film;
    }

    private void insertGenres(Film film) {
        String filmGenreQuery = "MERGE INTO FILM_GENRE(FILM_ID, GENRE_ID) KEY(FILM_ID, GENRE_ID)" +
                "VALUES (?, ?)";
        if (film.getGenres() != null || !film.getGenres().isEmpty()) {
            for (Genre genre : film.getGenres()) {
                jdbcTemplate.update(filmGenreQuery,
                        film.getId(),
                        genre.getId());
            }
        }
    }

    private void insertLikes(Film film) {
        String filmGenreQuery = "MERGE INTO LIKES(FILM_ID, USER_ID) KEY(FILM_ID, USER_ID)" +
                "VALUES (?, ?)";
        if (!film.getLikes().isEmpty()) {
            for (Integer userId : film.getLikes()) {
                jdbcTemplate.update(filmGenreQuery,
                        film.getId(),
                        userId);
            }
        }
    }

}
