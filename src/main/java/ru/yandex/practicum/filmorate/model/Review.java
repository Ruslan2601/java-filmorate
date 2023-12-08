package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.NotZero;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    private int reviewId;
    @NotBlank(message = "Содержание отзыва не может быть пустым или null")
    private String content;
    @NotNull(message = "Отзыв должен быть положительный(true) или отрицательный(false)")
    private Boolean isPositive;
    // Валидация @NotZero добавлена для получения другого кода ошибки при null значении переменной
    @NotZero
    private int userId;
    @NotZero
    private int filmId;
    private int useful;
}
