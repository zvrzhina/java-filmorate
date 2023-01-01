package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    static int idCounter = 1;
    private int id;
    @NotNull
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;

    private Set<Integer> likes; // Integer is userId

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Film(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("releaseDate") LocalDate releaseDate, @JsonProperty("duration") long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.id = idCounter++;
        this.likes = new HashSet<>();
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Film(@JsonProperty("id") int id, String name, String description, LocalDate releaseDate, long duration) {
        if (id == 0) {
            this.id = idCounter++; // user didn't pass id in json
        } else {
            this.id = id;
        }
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.likes = new HashSet<>();
    }

    public static void resetIdCounter() {
        idCounter = 1;
    }

    public static void decrementIdCounter() {
        --idCounter;
    }

    public void addLike(Integer userId) {
        this.likes.add(userId);
    }

    public void removeLike(Integer userId) {
        this.likes.remove(userId);
    }

    public Integer getLikesAmount() {
        return this.likes.size();
    }
}