package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.FilmService;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.impl.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.utils.gson.DurationDeserializer;
import ru.yandex.practicum.filmorate.utils.gson.DurationSerializer;
import ru.yandex.practicum.filmorate.utils.gson.LocalDateDeserializer;
import ru.yandex.practicum.filmorate.utils.gson.LocalDateSerializer;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
@ContextConfiguration(classes = {FilmController.class, InMemoryFilmStorage.class, FilmService.class, UserController.class, InMemoryUserStorage.class, UserService.class})
public class FilmControllerTest {
    private final String pattern = "yyyy-MM-dd";
    private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    GsonBuilder gsonBuilder;
    Gson gson;

    @BeforeEach
    public void setUp() {
        gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateDeserializer());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationDeserializer());
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gsonBuilder.registerTypeAdapter(Duration.class, new DurationSerializer());
        gson = gsonBuilder.setPrettyPrinting().create();
    }

    @AfterEach
    public void afterEach() {
        Film.resetIdCounter();
    }

    @Test
    void shouldCreateFilm() throws Exception {
        Film expected = new Film("Kill Bill 1", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.", LocalDate.of(2003, Month.OCTOBER, 10), 6660);
        String json = gson.toJson(expected);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/films")).andReturn();
        String content = result.getResponse().getContentAsString();
        Type listType = new TypeToken<List<Film>>() {
        }.getType();

        List<Film> actual = gson.fromJson(content, listType);
        Assertions.assertEquals(expected.getName(), actual.get(0).getName());
        Assertions.assertEquals(expected.getDescription(), actual.get(0).getDescription());
        Assertions.assertEquals(expected.getReleaseDate(), actual.get(0).getReleaseDate());
        Assertions.assertEquals(expected.getDuration(), actual.get(0).getDuration());
        Assertions.assertEquals(1, actual.get(0).getId());
    }

    @Test
    void nameCantBeEmpty() throws Exception {
        Film expected = new Film("", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.", LocalDate.of(2003, Month.OCTOBER, 10), 6660);
        String json = gson.toJson(expected);
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Название фильма не может быть пустым.");
        }
    }

    @Test
    void descriptionCantBe201Symbol() throws Exception {
        Film expected = new Film("Kill Bill 1", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior. 200 symbols. 200 symbols. 200 symbols. 200 symbols. 200 symbols. 200 sym", LocalDate.of(2003, Month.OCTOBER, 10), 6660);
        String json = gson.toJson(expected);
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Максимальная длина описания — 200 символов.");
        }
    }

    @Test
    void releaseDateCantBeBeforeTheFirstFilmRelease() throws Exception {
        Film expected = new Film("Kill Bill 1", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.", LocalDate.of(1985, Month.DECEMBER, 27), 6660);
        String json = gson.toJson(expected);
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json)
                            .accept(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Дата релиза — не раньше 28 декабря 1895 года.");
        }
    }

    @Test
    void durationShouldBePositive() throws Exception {
        Film expected1 = new Film("Kill Bill 1", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.", LocalDate.of(2003, Month.OCTOBER, 10), -1);
        String json1 = gson.toJson(expected1);
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json1)
                            .accept(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Продолжительность фильма должна быть положительной.");
        }

        Film expected2 = new Film("Kill Bill 1", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.", LocalDate.of(2003, Month.OCTOBER, 10), 0);
        String json2 = gson.toJson(expected2);
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json2)
                            .accept(MediaType.APPLICATION_JSON));
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Продолжительность фильма должна быть положительной.");
        }
    }

    @Test
    void shouldSetLike() throws Exception {
        Film film = new Film("Kill Bill 1", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.", LocalDate.of(2003, Month.OCTOBER, 10), 6660);
        String filmJson = gson.toJson(film);

        User user = new User("alina@yandex.ru", "Alina", "Alina Z", LocalDate.of(1990, Month.OCTOBER, 10));
        String userJson = gson.toJson(user);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/1")).andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/films/1")).andReturn();
        String content = result.getResponse().getContentAsString();

        Film actual = gson.fromJson(content, Film.class);
        Assertions.assertEquals(1, actual.getLikesAmount());
    }

    @Test
    void shouldDeleteLike() throws Exception {
        Film film = new Film("Kill Bill 1", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.", LocalDate.of(2003, Month.OCTOBER, 10), 6660);
        String filmJson = gson.toJson(film);

        User user = new User("alina@yandex.ru", "Alina", "Alina Z", LocalDate.of(1990, Month.OCTOBER, 10));
        String userJson = gson.toJson(user);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.put("/films/1/like/1")).andReturn();
        mockMvc.perform(MockMvcRequestBuilders.delete("/films/1/like/1")).andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/films/1")).andReturn();
        String content = result.getResponse().getContentAsString();

        Film actual = gson.fromJson(content, Film.class);
        Assertions.assertEquals(0, actual.getLikesAmount());
    }

    @Test
    void shouldGetFirstPopular() throws Exception {
        Film film1 = new Film("Kill Bill 1", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.", LocalDate.of(2003, Month.OCTOBER, 10), 6660);
        String filmJson1 = gson.toJson(film1);

        Film film2 = new Film("Kill Bill 2", "Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.", LocalDate.of(2003, Month.OCTOBER, 10), 6660);
        String filmJson2 = gson.toJson(film2);

        User user = new User("alina@yandex.ru", "Alina", "Alina Z", LocalDate.of(1990, Month.OCTOBER, 10));
        String userJson = gson.toJson(user);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson1)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(filmJson2)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(userJson)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        mockMvc.perform(MockMvcRequestBuilders.put("/films/2/like/1")).andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/films/popular?count=1")).andReturn();
        String content = result.getResponse().getContentAsString();

        Type listType = new TypeToken<List<Film>>() {
        }.getType();

        List<Film> actual = gson.fromJson(content, listType);
        Assertions.assertEquals(2, actual.get(0).getId());
        Assertions.assertEquals(1, actual.get(0).getLikesAmount());
    }

}
