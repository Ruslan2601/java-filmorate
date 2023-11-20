package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import ru.yandex.practicum.filmorate.validation.DateNonBefore;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.Set;

@Data
public class Film {
    private int id;
    @NotBlank(message = "Название фильма пустое или null")
    private String name;
    @Size(max = 200, message = "Длина описания превышает 200 симловов")
    private String description;
    @DateNonBefore
    private LocalDate releaseDate;
    @Positive(message = "Длительность фильма не положительна")
    private int duration;
    @NotNull(message = "mpa фильма не должно быть null")
    private Mpa mpa;
    private Set<Genre> genres;
    private Set<Integer> userLikes;
}