package com.springnote.api.utils.validation.number;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = NumberRangeCheckValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NumberRangeCheck {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    long min() default 0;

    long max() default Long.MAX_VALUE;

    boolean nullable() default true;

    String message() default "숫자 범위를 벗어났습니다!";
}
