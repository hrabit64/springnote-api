package com.springnote.api.utils.validation.list;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ListSizeCheckValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListSizeCheck {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int max() default 0;

    int min() default 0;

    boolean nullable() default true;

    String message() default "리스트의 크기가 범위를 벗어났습니다!";
}
