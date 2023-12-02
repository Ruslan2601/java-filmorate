package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class Review {
    private int reviewId;
    @NotBlank(message = "Содержание отзыва не может быть пустым или null")
    private String content;
    @NotNull(message = "Отзыв должен быть положительный(true) или отрицательный(false)")
    private Boolean isPositive;
    private int userId;
    private int filmId;
    private int useful;
}
