package com.springnote.api.utils.validation.list;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class ListSizeCheckValidator implements ConstraintValidator<ListSizeCheck, List<?>> {

    private int min;
    private int max;
    private boolean nullable;


    @Override
    public void initialize(ListSizeCheck constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
        this.nullable = constraintAnnotation.nullable();
    }

    @Override
    public boolean isValid(List<?> objects, ConstraintValidatorContext constraintValidatorContext) {
        if (objects == null) {
            return nullable;
        }
        return objects.size() >= min && objects.size() <= max;
    }
}
