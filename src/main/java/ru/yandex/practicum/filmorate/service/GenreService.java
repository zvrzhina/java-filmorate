package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.GenreStorage;

import java.util.List;

@Slf4j
@Service
public class GenreService {
    @Autowired
    private final GenreStorage genreStorage;

    @Autowired
    public GenreService(GenreStorage genreStorage) {
        this.genreStorage = genreStorage;
    }

    public Genre getGenre(Integer id) {
        Genre genre = genreStorage.getAll()
                .stream()
                .filter(g -> id.equals(g.getId()))
                .findAny()
                .orElse(null);
        if (genre == null) {
            throw new EntityNotFoundException(id, Entity.GENRE);
        }
        return genre;
    }

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }
}
