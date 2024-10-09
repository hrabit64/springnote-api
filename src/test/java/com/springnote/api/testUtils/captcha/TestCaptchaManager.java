package com.springnote.api.testUtils.captcha;

import com.springnote.api.security.captcha.CaptchaManager;
import org.springframework.stereotype.Component;

@Component
public class TestCaptchaManager implements CaptchaManager {

    @Override
    public boolean verify(String token) {
        return TestCaptchaToken.VALID_TOKEN.getToken().equals(token);
    }

}
