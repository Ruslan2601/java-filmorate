package ru.yandex.practicum.filmorate.validation;

import javax.validation.Constraint;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidationCorrectMpaId.class)
public @interface CorrectMpaId {
    String message() default "id для mpa должен быть числом >= {min} и <= {max}";

    Class<?>[] groups() default {};

    Class<?>[] payload() default {};

    int min() default 1;

    int max() default 5;
}
