package ru.yandex.practicum.filmorate.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ValidationDateNonBefore implements ConstraintValidator<DateNonBefore, LocalDate> {
    private LocalDate minDate;

    @Override
    public void initialize(DateNonBefore constraintAnnotation) {
        minDate = LocalDate.parse(constraintAnnotation.value());
    }

    @Override
    public boolean isValid(LocalDate date, ConstraintValidatorContext context) {
        return date == null || !date.isBefore(minDate);
    }
}