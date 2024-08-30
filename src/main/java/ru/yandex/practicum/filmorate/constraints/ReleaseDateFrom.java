package ru.yandex.practicum.filmorate.constraints;

import jakarta.validation.*;
import ru.yandex.practicum.filmorate.constraints.validators.ReleaseDateFromValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({ElementType.FIELD})
@Retention(RUNTIME)
@Documented
@Constraint(validatedBy = {
        ReleaseDateFromValidator.class
})
public @interface ReleaseDateFrom {
    String message() default "Date must be after {from}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String from();
}

