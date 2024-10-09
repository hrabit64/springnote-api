package com.springnote.api.security.captcha;

public record RecaptchaRequest(
        String secret,
        String response
) {
}
