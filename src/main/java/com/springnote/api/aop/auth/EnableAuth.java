package com.springnote.api.aop.auth;

import java.lang.annotation.*;

/**
 * 해당 메소드에 대해 인증을 활성화하는 어노테이션
 * 
 * @author 황준서('hzser123@gmail.com')
 * @since 1.0.0
 */
@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableAuth {
    AuthLevel authLevel() default AuthLevel.GUEST;
}