package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.impl.DbFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.DbGenreStorage;
import ru.yandex.practicum.filmorate.storage.impl.DbMpaStorage;
import ru.yandex.practicum.filmorate.storage.impl.DbUserStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@Sql({"/drop_schema.sql", "/schema.sql", "/data.sql", "/test_data.sql"})
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class DbTests {
    private final DbUserStorage userStorage;
    private final DbFilmStorage filmStorage;
    private final DbGenreStorage genreStorage;
    private final DbMpaStorage mpaStorage;

    @Test
    public void testGetUser() {
        Integer userId = 1;
        User user = userStorage.getAll().stream()
                .filter(u -> userId.equals(u.getId()))
                .findAny()
                .orElse(null);

        assertThat(user).hasFieldOrPropertyWithValue("id", 1);

    }

    @Test
    public void testGetAllUsers() {
        List<User> users = userStorage.getAll();
        assertThat(users.size()).isEqualTo(3);
    }

    @Test
    public void createUser() {
        Integer newId = 4;
        User newUser = new User("new@gmail.com", "new", "new", LocalDate.of(2022, 01, 01));
        userStorage.create(newUser);
        User userFromDb = userStorage.getAll().stream()
                .filter(u -> newId.equals(u.getId()))
                .findAny().orElse(null);
        assertThat(userFromDb).hasFieldOrPropertyWithValue("email", "new@gmail.com")
                .hasFieldOrPropertyWithValue("login", "new")
                .hasFieldOrPropertyWithValue("name", "new")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2022, 01, 01));
    }

    @Test
    public void updateUser() {
        Integer userId = 1;
        User updatedUser1 = new User(1, "updated@gmail.com", "UpdatedLogin", "UpdatedName", LocalDate.of(2022, 01, 01));
        userStorage.update(updatedUser1);
        User userFromDb = userStorage.getAll().stream()
                .filter(u -> userId.equals(u.getId()))
                .findAny().orElse(null);
        assertThat(userFromDb).hasFieldOrPropertyWithValue("email", "updated@gmail.com")
                .hasFieldOrPropertyWithValue("login", "UpdatedLogin")
                .hasFieldOrPropertyWithValue("name", "UpdatedName")
                .hasFieldOrPropertyWithValue("birthday", LocalDate.of(2022, 01, 01));
    }

    @Test
    public void testGetAllMpa() {
        List<Mpa> mpas = mpaStorage.getAll();
        assertThat(mpas.size()).isEqualTo(5);
    }

    @Test
    public void testGetAllGenres() {
        List<Genre> genres = genreStorage.getAll();
        assertThat(genres.size()).isEqualTo(6);
    }

    @Test
    public void testGetAllFilms() {
        List<Film> films = filmStorage.getAll();
        assertThat(films.size()).isEqualTo(3);
    }

    @Test
    public void createFilm() {
        Integer newId = 4;
        Film newFilm = new Film(new HashSet<>(), "new", "new", LocalDate.of(2022, 01, 01), 1111L, new Mpa(2, "PG"), new LinkedHashSet<>());
        filmStorage.create(newFilm);
        Film filmFromDb = filmStorage.getAll().stream()
                .filter(f -> newId.equals(f.getId()))
                .findAny().orElse(null);
        assertThat(filmFromDb).hasFieldOrPropertyWithValue("name", "new")
                .hasFieldOrPropertyWithValue("description", "new")
                .hasFieldOrPropertyWithValue("duration", 1111L)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2022, 01, 01))
                .hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
    }

    @Test
    public void updateFilm() {
        Integer filmId = 1;
        Film updatedFilm1 = new Film(1, "updated", "updated", LocalDate.of(2022, 01, 01), 1111, new Mpa(2, "PG"), new LinkedHashSet<>(), new HashSet<>());

        filmStorage.update(updatedFilm1);
        Film filmFromDb = filmStorage.getAll().stream()
                .filter(f -> filmId.equals(f.getId()))
                .findAny().orElse(null);
        assertThat(filmFromDb).hasFieldOrPropertyWithValue("name", "updated")
                .hasFieldOrPropertyWithValue("description", "updated")
                .hasFieldOrPropertyWithValue("duration", 1111L)
                .hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(2022, 01, 01))
                .hasFieldOrPropertyWithValue("mpa", new Mpa(2, "PG"));
    }
}
