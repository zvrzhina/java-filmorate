package ru.yandex.practicum.filmorate.storage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class DbMpaStorage implements MpaStorage {
    private final Logger log = LoggerFactory.getLogger(DbFilmStorage.class);
    private final JdbcTemplate jdbcTemplate;

    public DbMpaStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Mpa> getAll() {
        SqlRowSet mpaRows = jdbcTemplate.queryForRowSet("SELECT * FROM RATING");

        List<Mpa> mpas = new ArrayList<>();

        if (mpaRows.next() == false) {
            log.info("Список рейтингов пуст.");
            return Collections.emptyList();
        } else {
            do {
                Mpa mpa = new Mpa(
                        mpaRows.getInt("RATING_ID"),
                        mpaRows.getString("NAME"));

                log.info("Найден рейтинг: {} {}", mpa.getId(), mpa.getName());
                mpas.add(mpa);
            }
            while (mpaRows.next());
        }
        return mpas;
    }
}
