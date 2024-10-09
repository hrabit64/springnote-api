package com.springnote.api.utils.validation.bot;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = CheckCaptchaValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckCaptcha {
    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    String message() default "캡차 인증에 실패했습니다.";
}
