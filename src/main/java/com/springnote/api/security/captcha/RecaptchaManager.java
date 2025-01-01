package com.springnote.api.security.captcha;

import com.springnote.api.config.RecaptchaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Profile("!test")
@Slf4j
@RequiredArgsConstructor
@Component
public class RecaptchaManager implements CaptchaManager {
    private final RecaptchaConfig recaptchaConfig;
    private final RestTemplate restTemplate;


    public boolean verify(String token) {
        RecaptchaResponse response;
        try {
            response = getResponse(token);
        } catch (Exception e) {
            log.error("Recaptcha verification failed.", e);
            throw e;
        }

        log.debug("Recaptcha response: {}" ,response);

        return response.success();
    }

    private RecaptchaResponse getResponse(String token) {
        var url = recaptchaConfig.getUrl();
        var request = new RecaptchaRequest(recaptchaConfig.getSecretKey(), token);

        return restTemplate.postForObject(url, request, RecaptchaResponse.class);
    }

}
