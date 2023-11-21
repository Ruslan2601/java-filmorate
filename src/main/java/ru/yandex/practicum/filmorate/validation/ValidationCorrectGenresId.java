package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.model.Genre;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Set;

public class ValidationCorrectGenresId implements ConstraintValidator<CorrectGenresId, Set<Genre>> {
    private int min;
    private int max;

    @Override
    public void initialize(CorrectGenresId constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Set<Genre> genres, ConstraintValidatorContext context) {
        if (genres == null) {
            return true;
        }

        for (Genre genre : genres) {
            if (genre.getId() < min || genre.getId() > max) {
                return false;
            }
        }

        return true;
    }
}
