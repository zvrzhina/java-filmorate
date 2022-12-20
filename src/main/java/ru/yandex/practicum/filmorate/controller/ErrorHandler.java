package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@Slf4j
@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void handleEntityNotFoundException(final EntityNotFoundException e) {
        switch (e.getEntity()) {
            case FILM:
                log.info(String.format("Фильм c id \"%s\" не найден.", e.getEntityId()));
                break;
            case LIKE:
                log.info(String.format("Лайк c id пользователя \"%s\" не найден.", e.getEntityId()));
                break;
            case USER:
                log.info(String.format("Пользователь c id \"%s\" не найден.", e.getEntity()));
                break;
            case FRIEND:
                log.info(String.format("Друг c id \"%s\" не найден", e.getEntityId()));
                break;
            default:
                log.info(String.format("Сущность c id \"%s\" не найдена", e.getEntityId()));
                break;
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleValidationException(final ValidationException e) {
        log.info(e.getMessage());
    }
}
