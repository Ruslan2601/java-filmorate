package ru.yandex.practicum.filmorate.validation;

import ru.yandex.practicum.filmorate.model.Mpa;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class ValidationCorrectMpaId implements ConstraintValidator<CorrectMpaId, Mpa> {
    private int min;
    private int max;

    @Override
    public void initialize(CorrectMpaId constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Mpa mpa, ConstraintValidatorContext context) {
        return mpa == null || (mpa.getId() >= min && mpa.getId() <= max);
    }
}
