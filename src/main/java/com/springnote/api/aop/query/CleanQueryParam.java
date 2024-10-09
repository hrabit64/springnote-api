package com.springnote.api.aop.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * MultiValueMap<String, String> 타입의 쿼리파라미터에 사용.
 * pageable 관련 파라미터를 제거하는데 사용.
 *
 * @see CleanQueryParamAspect
 */
@Deprecated
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface CleanQueryParam {

}
