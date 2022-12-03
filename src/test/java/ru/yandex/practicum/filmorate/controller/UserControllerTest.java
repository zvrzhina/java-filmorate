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
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.LocalDateDeserializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Month;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
public class UserControllerTest {
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
        gson = gsonBuilder.setPrettyPrinting().create();
    }

    @Test
    void shouldCreateFilm() throws Exception {
        User expected = User.builder()
                .id(1)
                .email("alina@yandex.ru")
                .login("Alina")
                .name("Alina Z")
                .birthday(LocalDate.of(1990, Month.OCTOBER, 10)).build();

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("{ \"id\": 1, \"email\": \"alina@yandex.ru\", \"login\": \"Alina\", \"name\": \"Alina Z\", \"birthday\": \"" + LocalDate.of(1990, Month.OCTOBER, 10) + "\"}")
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users")).andReturn();
        String content = result.getResponse().getContentAsString();
        Type mapType = new TypeToken<Map<Integer, User>>() {
        }.getType();

        Map<Integer, User> actual = gson.fromJson(content, mapType);
        Assertions.assertEquals(expected, actual.get(1));
    }

    @Test
    void emailCantBeEmpty() throws Exception {
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{ \"id\": 1, \"email\": \"\", \"login\": \"Alina\", \"name\": \"Alina Z\", \"birthday\": \"" + LocalDate.of(1990, Month.OCTOBER, 10) + "\"}")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Некоректный адрес электронной почты. Проверьте введенное значение.");
        }
    }

    @Test
    void addressSignCantAbsent() throws Exception {
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{ \"id\": 1, \"email\": \"alinayandex.ru\", \"login\": \"Alina\", \"name\": \"Alina Z\", \"birthday\": \"" + LocalDate.of(1990, Month.OCTOBER, 10) + "\"}")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Некоректный адрес электронной почты. Проверьте введенное значение.");
        }
    }

    @Test
    void loginCantBeEmpty() throws Exception {
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{ \"id\": 1, \"email\": \"alina@yandex.ru\", \"login\": \"\", \"name\": \"Alina Z\", \"birthday\": \"" + LocalDate.of(1990, Month.OCTOBER, 10) + "\"}")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Логин не может быть пустым или содержать пробелы");
        }
    }

    @Test
    void loginShouldBeWithoutSpaces() throws Exception {
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{ \"id\": 1, \"email\": \"alina@yandex.ru\", \"login\": \"Alina Z\", \"name\": \"Alina Z\", \"birthday\": \"" + LocalDate.of(1990, Month.OCTOBER, 10) + "\"}")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Логин не может быть пустым или содержать пробелы");
        }
    }

    @Test
    void birthdayCantBeInTheFuture() throws Exception {
        LocalDate currentDate = LocalDate.now();
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{ \"id\": 1, \"email\": \"alina@yandex.ru\", \"login\": \"Alina\", \"name\": \"Alina Z\", \"birthday\": \"" + currentDate.plusDays(1) + "\"}")
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andReturn();
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
            assert (exception.getMessage()).equals("Дата рождения не может быть в будущем");
        }
    }
}
