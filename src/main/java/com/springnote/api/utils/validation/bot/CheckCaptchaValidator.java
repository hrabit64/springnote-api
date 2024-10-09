package com.springnote.api.utils.validation.bot;

import com.springnote.api.security.captcha.CaptchaManager;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CheckCaptchaValidator implements ConstraintValidator<CheckCaptcha, String> {

    private final CaptchaManager captchaManager;

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {

        return captchaManager.verify(s);
    }
}
