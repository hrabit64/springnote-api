package com.springnote.api.utils.validation.base64;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Base64;


public class Base64CheckValidator implements ConstraintValidator<Base64Check, String> {
    private final Base64.Decoder decoder = Base64.getDecoder();

    @Override
    public boolean isValid(String input, ConstraintValidatorContext constraintValidatorContext) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        try {
            decoder.decode(input);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
