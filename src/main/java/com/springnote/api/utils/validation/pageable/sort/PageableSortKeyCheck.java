package com.springnote.api.utils.validation.pageable.sort;

import com.springnote.api.domain.SortKeys;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Pageable 타입의 파라미터에 사용.
 * 정렬 옵션이 유효한 값인지 검증합니다.
 *
 * @see PageableSortKeyValidator
 */
@Constraint(validatedBy = PageableSortKeyValidator.class)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface PageableSortKeyCheck {

    String message() default "정렬 옵션이 잘못되었습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends SortKeys> sortKey();

}
