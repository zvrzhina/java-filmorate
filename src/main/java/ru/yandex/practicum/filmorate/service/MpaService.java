package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.model.Entity;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.MpaStorage;

import java.util.List;

@Slf4j
@Service
public class MpaService {
    @Autowired
    private final MpaStorage mpaStorage;

    @Autowired
    public MpaService(MpaStorage mpaStorage) {
        this.mpaStorage = mpaStorage;
    }

    public Mpa getMpa(Integer id) {
        Mpa mpa = mpaStorage.getAll()
                .stream()
                .filter(m -> id.equals(m.getId()))
                .findAny()
                .orElse(null);
        if (mpa == null) {
            throw new EntityNotFoundException(id, Entity.MPA);
        }
        return mpa;
    }

    public List<Mpa> getAll() {
        return mpaStorage.getAll();
    }
}
