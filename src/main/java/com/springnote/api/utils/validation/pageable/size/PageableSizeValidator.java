package com.springnote.api.utils.validation.pageable.size;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;

@Slf4j
public class PageableSizeValidator implements ConstraintValidator<PageableSizeCheck, Pageable> {

    private int min;
    private int max;

    @Override
    public void initialize(PageableSizeCheck constraintAnnotation) {
        min = constraintAnnotation.min();
        max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(Pageable value, ConstraintValidatorContext context) {
        log.debug("PageableSizeValidator isValid value: {}", value);
        log.debug("PageableSizeValidator isValid min: {}", min);
        log.debug("{}", value.getPageSize() >= min);
        return value.getPageSize() >= min && value.getPageSize() <= max;
    }
}
