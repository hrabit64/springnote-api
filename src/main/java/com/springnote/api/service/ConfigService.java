package com.springnote.api.service;


import com.springnote.api.domain.config.Config;
import com.springnote.api.domain.config.ConfigRepository;
import com.springnote.api.dto.config.common.ConfigResponseCommonDto;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ConfigService {

    private final ConfigRepository configRepository;

    @Cacheable(value = "config", key = "#key")
    @Transactional(readOnly = true)
    public String getConfig(String key) {
        var data = fetchConfigById(key);

        return data.getValue();
    }


    @CachePut(value = "config", key = "#key")
    @Transactional
    public ConfigResponseCommonDto updateConfig(String key, String value) {
        var config = fetchConfigById(key);

        config.setValue(value);
        var newConfig = configRepository.save(config);

        return new ConfigResponseCommonDto(newConfig);
    }


    private Config fetchConfigById(String key) {
        return configRepository.findById(key)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(key, "설정"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }


}
