package com.springnote.api.utils.validation.bot;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class EnableHoneypotValidator implements ConstraintValidator<EnableHoneypot, Boolean> {

    @Override
    public boolean isValid(Boolean aBoolean, ConstraintValidatorContext constraintValidatorContext) {
        return aBoolean == null || !aBoolean;
    }
}
