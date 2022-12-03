package ru.yandex.practicum.filmorate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.utils.DurationDeserializer;
import ru.yandex.practicum.filmorate.utils.LocalDateDeserializer;

import java.lang.reflect.Type;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FilmController.class)
public class FilmControllerTest {
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
        gson = gsonBuilder.setPrettyPrinting().create();
    }

    @Test
    void shouldCreateFilm() throws Exception {
        Film expected = Film.builder()
                .id(1)
                .name("Kill Bill 1")
                .description("Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.")
                .releaseDate(LocalDate.of(2003, Month.OCTOBER, 10))
                .duration(Duration.ofMinutes(111)).build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/films")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"id\": 1, \"name\": \"Kill Bill 1\", \"description\": \"Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.\", \"releaseDate\": \"" + LocalDate.of(2003, Month.OCTOBER, 10) + "\", \"duration\": \"" + Duration.ofMinutes(111).toString() + "\"}")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/films")).andReturn();
        String content = result.getResponse().getContentAsString();
        Type mapType = new TypeToken<Map<Integer, Film>>() {
        }.getType();

        Map<Integer, Film> actual = gson.fromJson(content, mapType);
        Assertions.assertEquals(expected, actual.get(1));
    }

    @Test
    void nameCantBeEmpty() throws Exception {
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"name\": \"\", \"description\": \"Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.\", \"releaseDate\": \"" + LocalDate.of(2003, Month.OCTOBER, 10) + "\", \"duration\": \"" + Duration.ofMinutes(111).toString() + "\"}")
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
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"name\": \"Kill Bill 1\", \"description\": \"Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior. 200 symbols. 200 symbols. 200 symbols. 200 symbols. 200 symbols. 200 sym\", \"releaseDate\": \"" + LocalDate.of(2003, Month.OCTOBER, 10) + "\", \"duration\": \"" + Duration.ofMinutes(111).toString() + "\"}")
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
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"name\": \"Kill Bill 1\", \"description\": \"Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.\", \"releaseDate\": \"" + LocalDate.of(1985, Month.DECEMBER, 27) + "\", \"duration\": \"" + Duration.ofMinutes(111).toString() + "\"}")
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
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"name\": \"Kill Bill 1\", \"description\": \"Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.\", \"releaseDate\": \"" + LocalDate.of(1985, Month.DECEMBER, 28) + "\", \"duration\": \"" + Duration.ofMinutes(-1).toString() + "\"}")
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
        try {
            mockMvc.perform(
                    MockMvcRequestBuilders.post("/films")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content("{ \"id\": 1, \"name\": \"Kill Bill 1\", \"description\": \"Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.\", \"releaseDate\": \"" + LocalDate.of(1985, Month.DECEMBER, 28) + "\", \"duration\": \"" + Duration.ofMinutes(0).toString() + "\"}")
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

}
