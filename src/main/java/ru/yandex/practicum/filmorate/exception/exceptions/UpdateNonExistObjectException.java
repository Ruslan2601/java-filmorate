package ru.yandex.practicum.filmorate.exception.exceptions;

public class UpdateNonExistObjectException extends RuntimeException {
    public UpdateNonExistObjectException(String message) {
        super(message);
    }
}