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
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.NestedServletException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.utils.gson.LocalDateDeserializer;
import ru.yandex.practicum.filmorate.utils.gson.LocalDateSerializer;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

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
        gsonBuilder.registerTypeAdapter(LocalDate.class, new LocalDateSerializer());
        gson = gsonBuilder.setPrettyPrinting().create();
    }

    @AfterEach
    public void afterEach() {
        User.resetIdCounter();
    }

    @Test
    void shouldCreateUser() throws Exception {
        User expected = new User("alina@yandex.ru", "Alina", "Alina Z", LocalDate.of(1990, Month.OCTOBER, 10));
        String json = gson.toJson(expected);

        mockMvc.perform(
                        MockMvcRequestBuilders.post("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/users")).andReturn();
        String content = result.getResponse().getContentAsString();
        Type listType = new TypeToken<List<User>>() {
        }.getType();

        List<User> actual = gson.fromJson(content, listType);
        Assertions.assertEquals(expected, actual.get(0));
    }

    @Test
    void emailCantBeEmpty() throws Exception {
        User expected = new User("", "Alina", "Alina Z", LocalDate.of(1990, Month.OCTOBER, 10));
        String json = gson.toJson(expected);
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json)
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
        User expected = new User("alinayandex.ru", "Alina", "Alina Z", LocalDate.of(1990, Month.OCTOBER, 10));
        String json = gson.toJson(expected);
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json)
                                    .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest())
                    .andReturn();
        } catch (NestedServletException e) {
            Exception exception =
                    assertThrows(
                            ValidationException.class,
                            () -> {
                                throw e.getCause();
                            });
        }
    }

    @Test
    void loginCantBeEmpty() throws Exception {
        User expected = new User("alina@yandex.ru", "", "Alina Z", LocalDate.of(1990, Month.OCTOBER, 10));
        String json = gson.toJson(expected);
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json)
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
        User expected = new User("alina@yandex.ru", "Alina Z", "Alina Z", LocalDate.of(1990, Month.OCTOBER, 10));
        String json = gson.toJson(expected);
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json)
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
        User expected = new User("alina@yandex.ru", "Alina", "Alina Z", currentDate.plusDays(1));
        String json = gson.toJson(expected);
        try {
            mockMvc.perform(
                            MockMvcRequestBuilders.post("/users")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(json)
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
