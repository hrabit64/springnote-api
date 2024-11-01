package com.springnote.api.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@Profile("!test")
@Configuration
public class RecaptchaConfig {

    @Value("${springnote.recaptcah.config.name}")
    private String recaptchaConfigName;

    @Getter
    private String siteKey;

    @Getter
    private String url;

    @Getter
    private String secretKey;

    public RecaptchaConfig() {
    }

    @PostConstruct
    public void init() throws IOException {
        //load recaptcha.json
        var config = new ClassPathResource(recaptchaConfigName).getInputStream();
        var mapper = new ObjectMapper();
        var node = mapper.readTree(config);

        siteKey = node.get("site_key").asText();
        secretKey = node.get("secret_key").asText();
        url = node.get("url").asText();
    }

    @Bean
    public RestTemplate recaptchaRestTemplate() {
        return new RestTemplate();
    }


}
