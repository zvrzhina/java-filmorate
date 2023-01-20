package ru.yandex.practicum.filmorate.storage.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
public class DbGenreStorage implements GenreStorage {
    private final JdbcTemplate jdbcTemplate;

    public DbGenreStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Genre> getAll() {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE");

        List<Genre> genres = new ArrayList<>();

        if (genreRows.next() == false) {
            log.info("Список жанров пуст.");
            return Collections.emptyList();
        } else {
            do {
                Genre genre = new Genre(
                        genreRows.getInt("GENRE_ID"),
                        genreRows.getString("NAME"));

                log.info("Найден жанр: {} {}", genre.getId(), genre.getName());
                genres.add(genre);
            }
            while (genreRows.next());
        }
        return genres;
    }

    @Override
    public Genre getGenre(Integer id) {
        SqlRowSet genreRows = jdbcTemplate.queryForRowSet("SELECT * FROM GENRE where GENRE_ID = ?", id);
        if (genreRows.next()) {
            Genre genre = new Genre(
                    genreRows.getInt("GENRE_ID"),
                    genreRows.getString("NAME"));
            log.info("Найден жанр: " + genreRows.getString("NAME"));
            return genre;
        } else {
            throw new EntityNotFoundException(id, Entity.GENRE);
        }
    }
}
