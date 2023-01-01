package ru.yandex.practicum.filmorate.exception;

import ru.yandex.practicum.filmorate.model.Entity;

public class EntityNotFoundException extends RuntimeException {
    private final Integer id;
    private final Entity entity;

    public EntityNotFoundException(Integer id, Entity entity) {
        this.id = id;
        this.entity = entity;
    }

    public Integer getEntityId() {
        return id;
    }

    public Entity getEntity() {
        return entity;
    }
}