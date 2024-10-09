package com.springnote.api.utils.validation.base64;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = Base64CheckValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Base64Check {
    String message() default "Base64 형식이 아닙니다!";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
