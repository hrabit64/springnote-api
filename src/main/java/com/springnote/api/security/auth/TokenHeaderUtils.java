package com.springnote.api.security.auth;

public class TokenHeaderUtils {
    /**
     * 주어진 토큰 헤더에서 토큰을 추출합니다.
     *
     * @param tokenHeader token header
     * @return token 만약 토큰 헤더가 잘못된 경우 null을 반환합니다.
     */
    public static String extractToken(String tokenHeader) {
        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
            return null;
        } else {
            return tokenHeader.substring(7);
        }

    }
}
