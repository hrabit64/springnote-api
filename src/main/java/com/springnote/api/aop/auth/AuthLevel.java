package com.springnote.api.aop.auth;

/**
 * 인증 옵션에 대하여 접근 가능한 Role Enum class
 * 
 * @author 황준서('hzser123@gmail.com')
 * @since 1.0.0
 */
public enum AuthLevel {

    // 로그인 + 블로그 관리자
    ADMIN, 

    // 로그인 
    USER, 

    // 로그인 X
    GUEST
}