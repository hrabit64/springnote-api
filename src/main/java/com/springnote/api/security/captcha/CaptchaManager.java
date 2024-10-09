package com.springnote.api.security.captcha;

public interface CaptchaManager {
    boolean verify(String token);
}
