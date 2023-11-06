package ru.yandex.practicum.filmorate.exception;

import lombok.Getter;

@Getter
public class ExceptionHandlerResponse {
    private final String message;
    private final String exception;

    public ExceptionHandlerResponse(String message, String exception) {
        this.message = message;
        this.exception = exception;
    }

    @Override
    public String toString() {
        return "ExceptionHandlerResponse{" +
                "message='" + message + '\'' +
                ", exception='" + exception + '\'' +
                '}';
    }
}