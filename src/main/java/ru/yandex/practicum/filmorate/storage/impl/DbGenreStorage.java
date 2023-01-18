package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DbGenreStorage implements GenreStorage {
    private final Logger log = LoggerFactory.getLogger(DbFilmStorage.class);
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
}
