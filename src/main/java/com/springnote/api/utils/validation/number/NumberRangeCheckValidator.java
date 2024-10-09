package com.springnote.api.utils.validation.number;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;


public class NumberRangeCheckValidator implements ConstraintValidator<NumberRangeCheck, Long> {
    private long min;
    private long max;
    private boolean nullable;

    @Override
    public void initialize(NumberRangeCheck constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(Long aLong, ConstraintValidatorContext constraintValidatorContext) {
        if (aLong == null) {
            return nullable;
        }
        return aLong >= min && aLong <= max;
    }
}
