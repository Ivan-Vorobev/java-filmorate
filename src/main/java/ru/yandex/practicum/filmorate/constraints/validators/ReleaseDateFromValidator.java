package ru.yandex.practicum.filmorate.constraints.validators;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import ru.yandex.practicum.filmorate.constraints.ReleaseDateFrom;

import java.time.LocalDate;

@SupportedValidationTarget(ValidationTarget.ANNOTATED_ELEMENT)
public class ReleaseDateFromValidator implements ConstraintValidator<ReleaseDateFrom, LocalDate> {
    private LocalDate dateFrom;

    @Override
    public void initialize(ReleaseDateFrom releaseDateFrom) {
        this.dateFrom = LocalDate.parse(releaseDateFrom.from());
    }

    @Override
    public boolean isValid(
            LocalDate fieldValue,
            ConstraintValidatorContext constraintContext) {

        return dateFrom == null || fieldValue.isAfter(dateFrom);
    }
}