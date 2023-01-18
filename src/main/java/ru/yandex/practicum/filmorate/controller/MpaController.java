package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.MpaService;

import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/mpa")
public class MpaController {
    private final MpaService mpaService;

    @Autowired
    public MpaController(MpaService mpaService) {
        this.mpaService = mpaService;
    }

    @GetMapping
    public List<Mpa> getAll() {
        log.info("Все рейтинги: {}", mpaService.getAll());
        return new ArrayList<>(mpaService.getAll());
    }

    @GetMapping("/{id}")
    public Mpa getMpa(@PathVariable("id") Integer id) {
        Mpa mpa = mpaService.getMpa(id);
        if (mpa == null) {
            throw new EntityNotFoundException(id, Entity.MPA);
        }
        return mpaService.getMpa(id);
    }
}
