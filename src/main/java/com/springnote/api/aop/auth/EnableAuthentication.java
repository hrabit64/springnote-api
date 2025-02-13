package com.springnote.api.aop.auth;

import java.lang.annotation.*;


/**
 * 해당 메소드에 대한 인증을 활성화 합니다.
 *
 * @see AuthLevel
 * @see EnableAuthAspect
 */
@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableAuthentication {
    // 권한 레벨
    AuthLevel value() default AuthLevel.NONE;

    // 비활성화된 사용자의 요청을 허용할지 여부
    boolean isAllowDisableUser() default false;
}
