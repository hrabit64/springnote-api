package com.springnote.api.aop.auth;

import java.lang.annotation.*;

@Inherited
@Retention(value = RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface EnableAuthentication {
    AuthLevel value() default AuthLevel.NONE;
}
