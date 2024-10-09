package com.springnote.api.config;

import com.springnote.api.service.ConfigService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

@Slf4j
@RequiredArgsConstructor
@Getter
@Configuration
public class AuthConfig {

    private final ConfigService configService;

    private final String confKeyEnableRegister = "auth.register.enable";
    private final String confKeyDefaultProfileImg = "auth.default.profile-img";


    public boolean isEnableRegister() {
        return Boolean.parseBoolean(configService.getConfig(confKeyEnableRegister));
    }

    public String getDefaultProfileImg() {
        return configService.getConfig(confKeyDefaultProfileImg);
    }


}
