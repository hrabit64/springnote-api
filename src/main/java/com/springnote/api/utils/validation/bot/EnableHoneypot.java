package com.springnote.api.utils.validation.bot;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = EnableHoneypotValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EnableHoneypot {

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


    String message() default "you just activated my honeypot!";
}
