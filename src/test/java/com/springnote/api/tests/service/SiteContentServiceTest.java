package com.springnote.api.tests.service;

import com.springnote.api.domain.siteContent.SiteContentRepository;
import com.springnote.api.dto.siteContent.service.SiteContentCreateRequestServiceDto;
import com.springnote.api.dto.siteContent.service.SiteContentUpdateRequestServiceDto;
import com.springnote.api.service.SiteContentService;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static com.springnote.api.testUtils.dataFactory.siteContent.SiteContentTestDataFactory.createSiteContent;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DisplayName("Service Test - SiteContent")
public class SiteContentServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private SiteContentService siteContentService;

    @Mock
    private SiteContentRepository siteContentRepository;

    @DisplayName("siteContentService.getSiteContentByKey")
    @Nested
    class getSiteContentById {

        @DisplayName("올바른 SiteContent 의 Key 를 입력하면 SiteContent 를 반환한다.")
        @Test
        void getSiteContentByKey_success() {
            // given
            var validKey = "test";
            var siteContent = createSiteContent(validKey);
            doReturn(Optional.of(createSiteContent(validKey))).when(siteContentRepository).findById(validKey);

            // when
            var result = siteContentService.getSiteContentById(validKey);

            // then
            assertNotNull(siteContent);
            assertEquals(validKey, result.getKey());
            assertEquals(siteContent.getValue(), result.getContent());

            verify(siteContentRepository).findById(validKey);
        }

        @DisplayName("올바르지 않은 SiteContent 의 Key 를 입력하면 Business Exception 을 반환한다.")
        @Test
        void getSiteContentByKey_fail() {
            // given
            var invalidKey = "invalid";

            doReturn(Optional.empty()).when(siteContentRepository).findById(invalidKey);

            // when
            var result = assertThrows(BusinessException.class, () -> siteContentService.getSiteContentById(invalidKey));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(siteContentRepository).findById(invalidKey);
        }
    }

    @DisplayName("siteContentService.create")
    @Nested
    class create {

        @DisplayName("올바른 SiteContent 를 입력하면 SiteContent 를 생성한다.")
        @Test
        void create_success() {
            // given
            var request = SiteContentCreateRequestServiceDto.builder()
                    .key("test")
                    .value("test content")
                    .build();

            var siteContent = createSiteContent();
            doReturn(siteContent).when(siteContentRepository).save(siteContent);

            // when
            var result = siteContentService.create(request);

            // then
            assertNotNull(result);
            assertEquals(siteContent.getKey(), result.getKey());
            assertEquals(siteContent.getValue(), result.getContent());

            verify(siteContentRepository).save(siteContent);
        }
    }

    @DisplayName("siteContentService.update")
    @Nested
    class update {

        @DisplayName("올바른 SiteContent 를 입력하면 SiteContent 를 수정한다.")
        @Test
        void update_success() {
            // given
            var key = "test";
            var request = SiteContentUpdateRequestServiceDto.builder()
                    .key(key)
                    .value("test content")
                    .build();

            var siteContent = createSiteContent(key);
            doReturn(Optional.of(siteContent)).when(siteContentRepository).findById(key);
            doReturn(siteContent).when(siteContentRepository).save(siteContent);

            // when
            var result = siteContentService.update(request);

            // then
            assertNotNull(result);
            assertEquals(siteContent.getKey(), result.getKey());
            assertEquals(siteContent.getValue(), result.getContent());

            verify(siteContentRepository).findById(key);
            verify(siteContentRepository).save(siteContent);
        }

        @DisplayName("존재하지 않는 SiteContent 를 입력하면 Business Exception 을 반환한다.")
        @Test
        void update_fail() {
            // given
            var key = "invalid";
            var request = SiteContentUpdateRequestServiceDto.builder()
                    .key(key)
                    .value("test content")
                    .build();

            doReturn(Optional.empty()).when(siteContentRepository).findById(key);

            // when
            var result = assertThrows(BusinessException.class, () -> siteContentService.update(request));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(siteContentRepository).findById(key);
        }
    }

    @DisplayName("siteContentService.delete")
    @Nested
    class delete {

        @DisplayName("올바른 SiteContent 의 Key 를 입력하면 SiteContent 를 삭제한다.")
        @Test
        void delete_success() {
            // given
            var key = "test";
            var siteContent = createSiteContent(key);
            doReturn(Optional.of(siteContent)).when(siteContentRepository).findById(key);

            // when
            siteContentService.delete(key);

            // then
            verify(siteContentRepository).findById(key);
            verify(siteContentRepository).delete(siteContent);
        }

        @DisplayName("존재하지 않는 SiteContent 를 입력하면 Business Exception 을 반환한다.")
        @Test
        void delete_fail() {
            // given
            var key = "invalid";
            doReturn(Optional.empty()).when(siteContentRepository).findById(key);

            // when
            var result = assertThrows(BusinessException.class, () -> siteContentService.delete(key));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(siteContentRepository).findById(key);
        }
    }
}
