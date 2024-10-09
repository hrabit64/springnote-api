package com.springnote.api.utils.validation.pageable.size;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Pageable 타입의 파라미터에 사용.
 * 페이지 사이즈가 유효한 값인지 검증합니다.
 *
 * @see PageableSizeValidator
 */
@Constraint(validatedBy = PageableSizeValidator.class)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageableSizeCheck {

    String message() default "페이징 사이즈가 잘못되었습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min() default 1;

    int max() default 20;

}
