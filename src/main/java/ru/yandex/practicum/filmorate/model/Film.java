package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
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
    private Mpa mpa;
    private Set<Genre> genres;
    private Set<Integer> likes; // Integer is userId

    public Film(String name, String description, LocalDate releaseDate, long duration) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.id = idCounter++;
        this.likes = new HashSet<>();
    }

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public Film(@JsonProperty("likes") Set<Integer> likes, @JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("releaseDate") LocalDate releaseDate, @JsonProperty("duration") long duration, @JsonProperty("mpa") Mpa mpa, @JsonProperty("genres") LinkedHashSet<Genre> genres) {
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = (genres == null) ? new LinkedHashSet<>() : new LinkedHashSet<>(genres);
        this.likes = (likes == null) ? new HashSet<>() : likes;
    }

    public Film(int id, String name, String description, LocalDate releaseDate, long duration, Mpa mpa, LinkedHashSet<Genre> genres, Set<Integer> likes) {
        if (id == 0) {
            this.id = idCounter++; // user didn't pass id in json
        } else {
            this.id = id;
        }
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
        this.genres = (genres == null) ? new LinkedHashSet<>() : new LinkedHashSet<>(genres);
        this.likes = (likes == null) ? new HashSet<>() : likes; // new film doesn't have likes initially
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

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public long getDuration() {
        return duration;
    }

    public Mpa getMpa() {
        return mpa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Film film = (Film) o;

        if (id != film.id) return false;
        if (duration != film.duration) return false;
        if (!name.equals(film.name)) return false;
        if (!description.equals(film.description)) return false;
        if (!releaseDate.equals(film.releaseDate)) return false;
        if (!mpa.equals(film.mpa)) return false;
        if (!genres.equals(film.genres)) return false;
        return Objects.equals(likes, film.likes);
    }

    @Override
    public int hashCode() {
        int result = id;
        result = 31 * result + name.hashCode();
        result = 31 * result + description.hashCode();
        result = 31 * result + releaseDate.hashCode();
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + mpa.hashCode();
        result = 31 * result + genres.hashCode();
        result = 31 * result + (likes != null ? likes.hashCode() : 0);
        return result;
    }
}