package ru.yandex.practicum.filmorate.model;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;
import java.util.Set;

@lombok.Data
public class User {
    private int id;
    @Email(message = "Не правильный email")
    private String email;
    @NotBlank(message = "Логин пустой или null")
    @Pattern(regexp = "^\\S*$", message = "Логин содержит символ пробела")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата рождения из будущего")
    private LocalDate birthday;
    private Set<Integer> friends;
}