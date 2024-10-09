package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.config.Config;
import com.springnote.api.domain.config.ConfigRepository;
import com.springnote.api.testUtils.template.RepositoryTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Repository Test - Config")
public class ConfigRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private ConfigRepository configRepository;


    @Nested
    @DisplayName("configRepository.findById")
    class findById {

        @DisplayName("올바른 Config 의 Key 를 입력하면 Config 를 반환한다.")
        @DataSet(value = "datasets/repository/config/base-config.yaml")
        @Test
        void findById_successWithValidKey() {
            // given
            var validKey = "test1";

            // when
            var config = configRepository.findById(validKey).orElse(null);

            // then
            assertNotNull(config);
            assertEquals(validKey, config.getKey());
        }

        @DisplayName("올바르지 않은 Config 의 Key 를 입력하면 Config 를 반환하지 않는다.")
        @DataSet(value = "datasets/repository/config/base-config.yaml")
        @Test
        void findById_failWithInvalidKey() {
            // given
            var invalidKey = "invalid";

            // when
            var config = configRepository.findById(invalidKey).orElse(null);

            // then
            assertNull(config);

        }
    }

    @Nested
    @DisplayName("configRepository.save")
    class save {

        @DisplayName("올바른 Config 를 입력하면 Config 를 저장한다.")
        @DataSet(value = "datasets/repository/config/empty-config.yaml")
        @ExpectedDataSet(value = "datasets/repository/config/saved-config.yaml")
        @Test
        void save_success() {
            // given
            var key = "test";
            var value = "test value";
            var config = Config.builder()
                    .key(key)
                    .value(value)
                    .build();

            // when
            var savedConfig = configRepository.save(config);
            configRepository.flush();

            // then
            assertEquals(key, savedConfig.getKey());
            assertEquals(value, savedConfig.getValue());
        }

//        @DisplayName("올바르지 않은 Key를 사용한 Config 를 입력하면 , 오류가 발생한다.")
//        @DataSet(value = "datasets/repository/config/empty-config.yaml")
//        @ExpectedDataSet(value = "datasets/repository/config/empty-config.yaml")
//        @Test
//        void save_failWithTooLongKey() {
//            // given
//            var tooLongKey = "a".repeat(301); // 300자 초과의 Key
//            var value = "test value";
//            var config = Config.builder()
//                    .key(tooLongKey)
//                    .value(value)
//                    .build();
//
//            // when
//            assertThrows(Exception.class, () -> {
//                configRepository.save(config);
//                configRepository.flush();
//            });
//        }
//
//        @DisplayName("올바르지 않은 value를 사용한 Config 를 입력하면 , 오류가 발생한다.")
//        @DataSet(value = "datasets/repository/config/empty-config.yaml")
//        @ExpectedDataSet(value = "datasets/repository/config/empty-config.yaml")
//        @Test
//        void save_failWithTooLongValue() {
//            // given
//            var key = "test";
//            var tooLongValue = "a".repeat(301); // 300자 초과의 Value
//
//            var config = Config.builder()
//                    .key(key)
//                    .value(tooLongValue)
//                    .build();
//
//            // when
//            assertThrows(Exception.class, () -> {
//                configRepository.save(config);
//                configRepository.flush();
//            });
//        }

    }

}
