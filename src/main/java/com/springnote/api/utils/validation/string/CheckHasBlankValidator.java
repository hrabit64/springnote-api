package com.springnote.api.utils.validation.string;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CheckHasBlankValidator implements ConstraintValidator<CheckHasBlank, String> {

    @Override
    public void initialize(CheckHasBlank constraintAnnotation) {
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && !value.contains(" ");
    }
}
