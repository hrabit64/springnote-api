package com.springnote.api.testUtils.captcha;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TestCaptchaToken {
    VALID_TOKEN("valid_token");

    private final String token;
}
