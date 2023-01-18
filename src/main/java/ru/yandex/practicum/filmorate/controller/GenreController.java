package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getAll() {
        log.info("Все жанры: {}", genreService.getAll());
        return new ArrayList<>(genreService.getAll());
    }

    @GetMapping("/{id}")
    public Genre getGenre(@PathVariable("id") Integer id) {
        Genre genre = genreService.getGenre(id);
        if (genre == null) {
            throw new EntityNotFoundException(id, Entity.GENRE);
        }
        return genreService.getGenre(id);
    }
}
