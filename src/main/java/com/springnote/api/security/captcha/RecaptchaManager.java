package com.springnote.api.security.captcha;

import com.springnote.api.config.RecaptchaConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
        if(!response.success()){
            log.info("Recaptcha verification failed. {}", response);
            return false;
        } else {
            log.debug("Recaptcha verification success.");

        }

        return response.score() > 0.5;
    }

    private RecaptchaResponse getResponse(String token) {
        var apiUrl = recaptchaConfig.getUrl();
        var url = UriComponentsBuilder.fromHttpUrl(apiUrl)
                .queryParam("secret", recaptchaConfig.getSecretKey())
                .queryParam("response", token)
                .build()
                .toUriString();

        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        var requestEntity = new HttpEntity<>(headers);
        return restTemplate.postForObject(url,requestEntity,RecaptchaResponse.class);
    }

}
