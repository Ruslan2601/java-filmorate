package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Director {
    private int id;
    @NotBlank(message = "Поле name режиссёра должно быть заполнено")
    private String name;
}
