package com.springnote.api.tests.service;

import com.springnote.api.domain.config.Config;
import com.springnote.api.domain.config.ConfigRepository;
import com.springnote.api.dto.config.common.ConfigResponseCommonDto;
import com.springnote.api.service.ConfigService;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DisplayName("Service Test - ConfigService")
public class ConfigServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private ConfigService configService;

    @Mock
    private ConfigRepository configRepository;

    @Nested
    @DisplayName("configService.getConfig")
    class getConfig {

        @DisplayName("정상적인 설정 Key가 주어지면, 해당 설정 값을 반환한다.")
        @Test
        void getConfig_success() {
            // given
            var validKey = "valid";

            var targetConfig = Config.builder()
                    .key(validKey)
                    .value("value")
                    .build();

            doReturn(Optional.of(targetConfig)).when(configRepository).findById(validKey);
            // when
            var result = configService.getConfig(validKey);

            // then
            assertEquals(targetConfig.getValue(), result);
            verify(configRepository).findById(validKey);
        }

        @DisplayName("존재하지 않는 설정 Key가 주어지면, 예외를 발생시킨다.")
        @Test
        void getConfig_fail() {
            // given
            var invalidKey = "invalid";

            doReturn(Optional.empty()).when(configRepository).findById(invalidKey);

            // when
            var result = assertThrows(BusinessException.class, () -> configService.getConfig(invalidKey));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(configRepository).findById(invalidKey);
        }
    }

    @DisplayName("configService.updateConfig")
    @Nested
    class updateConfig {

        @DisplayName("정상적인 설정 Key와 설정 값이 주어지면, 설정 값을 업데이트한다.")
        @Test
        void updateConfig_success() {
            // given
            var validKey = "valid";
            var validValue = "value";

            var targetConfig = Config.builder()
                    .key(validKey)
                    .value("oldValue")
                    .build();

            var updatedConfig = Config.builder()
                    .key(validKey)
                    .value(validValue)
                    .build();

            doReturn(Optional.of(targetConfig)).when(configRepository).findById(validKey);
            doReturn(updatedConfig).when(configRepository).save(updatedConfig);

            // when
            var result = configService.updateConfig(validKey, validValue);

            // then
            var expected = ConfigResponseCommonDto.builder()
                    .key(validKey)
                    .value(validValue)
                    .build();

            assertEquals(expected, result);
            verify(configRepository).save(targetConfig);
            verify(configRepository).findById(validKey);
        }

        @DisplayName("존재하지 않는 설정 Key가 주어지면, 예외를 발생시킨다.")
        @Test
        void updateConfig_fail() {
            // given
            var invalidKey = "invalid";
            var validValue = "value";

            doReturn(Optional.empty()).when(configRepository).findById(invalidKey);

            // when
            var result = assertThrows(BusinessException.class, () -> configService.getConfig(invalidKey));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(configRepository).findById(invalidKey);
        }
    }
}
