package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;

@Data
@Builder
public class Film {
    private int id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Duration duration;

    public static void main(String[] args) {
        Film film = Film.builder()
                .id(1)
                .name("Kill Bill 1")
                .description("Kill Bill is the story of one retired assassin's revenge against a man who tried to kill her while she was pregnant years prior.")
                .releaseDate(LocalDate.of(2003, Month.OCTOBER, 10))
                .duration(Duration.ofMinutes(111)).build();
    }
}