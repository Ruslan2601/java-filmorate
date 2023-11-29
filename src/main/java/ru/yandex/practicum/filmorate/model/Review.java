package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class Review {
    private int id;
    @NotBlank(message = "Содержание отзыва не может быть пустым или null")
    private String content;
    @NotNull(message = "Отзыв должен быть положительный(true) или отрицательный(false)")
    private boolean isPositive;
    @Positive(message = "id пользовителя должен быть положительным числом")
    private int userId;
    @Positive(message = "id фильма должен быть положительным числом")
    private int filmId;
    private int useful;
}
