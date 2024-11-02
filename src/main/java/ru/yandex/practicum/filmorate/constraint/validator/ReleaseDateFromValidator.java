package ru.yandex.practicum.filmorate.constraint.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.constraintvalidation.SupportedValidationTarget;
import jakarta.validation.constraintvalidation.ValidationTarget;
import ru.yandex.practicum.filmorate.constraint.ReleaseDateFrom;

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