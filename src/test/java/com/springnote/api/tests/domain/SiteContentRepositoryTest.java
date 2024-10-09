package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.siteContent.SiteContent;
import com.springnote.api.domain.siteContent.SiteContentRepository;
import com.springnote.api.testUtils.template.RepositoryTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Repository Test - SiteContent")
public class SiteContentRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private SiteContentRepository siteContentRepository;

    @DisplayName("siteContentRepository.findById")
    @Nested
    class findById {
        @DataSet(value = "datasets/repository/siteContent/base-siteContent.yaml")
        @DisplayName("올바른 SiteContent 의 Id 를 입력하면 SiteContent 를 반환한다.")
        @Test
        void findById_success() {
            // given
            var validId = "test";

            // when
            var siteContent = siteContentRepository.findById(validId).orElse(null);

            // then
            assertNotNull(siteContent);
            assertEquals(validId, siteContent.getKey());
        }

        @DataSet(value = "datasets/repository/siteContent/base-siteContent.yaml")
        @DisplayName("올바르지 않은 SiteContent 의 Id 를 입력하면 SiteContent 를 반환하지 않는다.")
        @Test
        void findById_fail() {
            // given
            var invalidId = "invalid";

            // when
            var siteContent = siteContentRepository.findById(invalidId).orElse(null);

            // then
            assertNull(siteContent);
        }
    }

    @DisplayName("siteContentRepository.save")
    @Nested
    class save {
        @DataSet(value = "datasets/repository/siteContent/empty-siteContent.yaml")
        @ExpectedDataSet(value = "datasets/repository/siteContent/base-siteContent.yaml")
        @DisplayName("올바른 SiteContent 가 주어지면, 해당 SiteContent 를 저장한다.")
        @Test
        void save_successWithValidSiteContent() {
            // given
            var siteContent = SiteContent.builder()
                    .key("test")
                    .value("test value")
                    .build();

            // when
            siteContentRepository.save(siteContent);
            siteContentRepository.flush();
        }
    }

    @DisplayName("siteContentRepository.existsByKey")
    @Nested
    class existsByKey {
        @DataSet(value = "datasets/repository/siteContent/base-siteContent.yaml")
        @DisplayName("존재하는 SiteContent 의 Key 를 입력하면 true 를 반환한다.")
        @Test
        void existsByKey_successWithValidKey() {
            // given
            var validKey = "test";

            // when
            var result = siteContentRepository.existsByKey(validKey);

            // then
            assertTrue(result);
        }

        @DataSet(value = "datasets/repository/siteContent/base-siteContent.yaml")
        @DisplayName("존재하지 않는 SiteContent 의 Key 를 입력하면 false 를 반환한다.")
        @Test
        void existsByKey_failWithInvalidKey() {
            // given
            var invalidKey = "invalid";

            // when
            var result = siteContentRepository.existsByKey(invalidKey);

            // then
            assertFalse(result);
        }
    }


}


