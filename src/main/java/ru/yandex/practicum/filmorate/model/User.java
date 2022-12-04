package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    static int idCounter = 1;
    private int id;
    private String email;
    private String login;
    private String name;
    private LocalDate birthday;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public User(@JsonProperty("email") String email, @JsonProperty("login") String login, @JsonProperty("name") String name, @JsonProperty("birthday") LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.id = idCounter++;
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public User(@JsonProperty("id") int id, @JsonProperty("email") String email, @JsonProperty("login") String login, @JsonProperty("name") String name, @JsonProperty("birthday") LocalDate birthday) {
        if (id == 0) {
            this.id = idCounter++; // user didn't pass id in json
        } else {
            this.id = id;
        }
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
    }

    public static void resetIdCounter() {
        idCounter = 1;
    }

    public static void decrementIdCounter() {
        --idCounter;
    }
}
