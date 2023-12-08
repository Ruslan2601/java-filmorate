package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class Director {
    private int id;
    @NotBlank(message = "Поле name режиссёра должно быть заполнено")
    @Size(max = 255)
    private String name;
}
