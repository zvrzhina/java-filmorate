package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDate;

@Data
public class Film {
    static int idCounter = 1;
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private long duration;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Film(@JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("releaseDate") LocalDate releaseDate, @JsonProperty("duration") long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.id = idCounter++;
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
    }

    public static void resetIdCounter() {
        idCounter = 1;
    }

    public static void decrementIdCounter() {
        --idCounter;
    }
}