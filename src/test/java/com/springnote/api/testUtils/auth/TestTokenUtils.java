package com.springnote.api.testUtils.auth;


import org.springframework.http.HttpHeaders;

public class TestTokenUtils {

    public static HttpHeaders createTokenHeader(TestFirebaseToken token) {
        var headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token.getToken()); // 인증 토큰 추가

        return headers;
    }
}
