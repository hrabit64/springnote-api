package com.springnote.api.aop.auth;

/**
 * 권한 레벨
 * <p>
 * NONE : 인증 없이 접근 가능 ( 인증이 필수는 아니나, 사용자 정보가 필요한 경우 사용 )
 * USER : 서비스내 등록이 된 사용자만 접근 가능
 * ADMIN : 관리자만 접근 가능
 */
public enum AuthLevel {
    NONE, USER, ADMIN
}
