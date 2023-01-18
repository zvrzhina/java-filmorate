package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
        return genreStorage.getAll()
                .stream()
                .filter(genre -> id.equals(genre.getId()))
                .findAny()
                .orElse(null);
    }

    public List<Genre> getAll() {
        return genreStorage.getAll();
    }
}
