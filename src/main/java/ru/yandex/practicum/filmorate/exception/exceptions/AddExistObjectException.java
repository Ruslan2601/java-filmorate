package ru.yandex.practicum.filmorate.exception.exceptions;

public class AddExistObjectException extends RuntimeException {
    public AddExistObjectException(String message) {
        super(message);
    }
}