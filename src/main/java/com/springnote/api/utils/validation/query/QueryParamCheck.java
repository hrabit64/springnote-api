package com.springnote.api.utils.validation.query;

import com.springnote.api.domain.QueryKeys;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MultiValueMap<String, String> 타입의 쿼리파라미터에 사용.
 * 쿼리파라미터에 유효한 키가 들어있는지 검증합니다.
 * 해당 쿼리파라미터들에 유효한 값이 들어있는지 검증합니다.
 *
 * @see QueryParamValidator
 */
@Constraint(validatedBy = QueryParamValidator.class)
@Target({ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface QueryParamCheck {

    String message() default "잘못된 쿼리 옵션이 포함되어 있습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends QueryKeys> queryKey();

}