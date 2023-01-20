package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yandex.practicum.filmorate.exception.EntityNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.ErrorResponse;

@Slf4j
@RestControllerAdvice("ru.yandex.practicum.filmorate.controller")
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(final EntityNotFoundException e) {
        String msg;
        switch (e.getEntity()) {
            case FILM:
                msg = String.format("Фильм c id \"%s\" не найден.", e.getEntityId());
                log.info(msg);
                return new ErrorResponse(msg);
            case LIKE:
                msg = String.format("Лайк c id пользователя \"%s\" не найден.", e.getEntityId());
                log.info(msg);
                return new ErrorResponse(msg);
            case USER:
                msg = String.format("Пользователь c id \"%s\" не найден.", e.getEntity());
                log.info(msg);
                return new ErrorResponse(msg);
            case FRIEND:
                msg = String.format("Друг c id \"%s\" не найден", e.getEntityId());
                log.info(msg);
                return new ErrorResponse(msg);
            case GENRE:
                msg = String.format("Жанр c id \"%s\" не найден", e.getEntityId());
                log.info(msg);
                return new ErrorResponse(msg);
            case MPA:
                msg = String.format("Рейтинг c id \"%s\" не найден", e.getEntityId());
                log.info(msg);
                return new ErrorResponse(msg);
            default:
                msg = String.format("Сущность c id \"%s\" не найдена", e.getEntityId());
                log.info(msg);
                return new ErrorResponse(msg);
        }
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final ValidationException e) {
        log.info(e.getMessage());
        return new ErrorResponse(e.getMessage());
    }
}
