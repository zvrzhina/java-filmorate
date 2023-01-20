package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Data
public class User {
    static int idCounter = 1;
    private int id;
    @Email
    private String email;
    @NotNull
    private String login;
    private String name;
    private LocalDate birthday;
    private Map<Integer, Boolean> friends;

    public User(String email, String login, String name, LocalDate birthday) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.id = idCounter++;
        this.friends = new HashMap<>();
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public User(@JsonProperty("email") String email, @JsonProperty("login") String login, @JsonProperty("name") String name, @JsonProperty("birthday") LocalDate birthday, @JsonProperty("friends") Map<Integer, Boolean> friends) {
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }

    public User(int id, String email, String login, String name, LocalDate birthday) {
        if (id == 0) {
            this.id = idCounter++; // user didn't pass id in json
        } else {
            this.id = id;
        }
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = new HashMap<>();
    }

    public User(int id, String email, String login, String name, LocalDate birthday, Map<Integer, Boolean> friends) {
        if (id == 0) {
            this.id = idCounter++; // user didn't pass id in json
        } else {
            this.id = id;
        }
        this.email = email;
        this.login = login;
        this.name = name;
        this.birthday = birthday;
        this.friends = friends;
    }

    public void addFriend(Integer id) {
        this.friends.put(id, false);
    }

    public void removeFriend(Integer id) {
        this.friends.remove(id);
    }

    public static void resetIdCounter() {
        idCounter = 1;
    }

    public static void decrementIdCounter() {
        --idCounter;
    }
}
