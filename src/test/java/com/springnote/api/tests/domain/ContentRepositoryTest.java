package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.content.Content;
import com.springnote.api.domain.content.ContentRepository;
import com.springnote.api.testUtils.template.RepositoryTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Repository Test - Content")
public class ContentRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private ContentRepository contentRepository;


    @Nested
    @DisplayName("contentRepository.findById")
    class findById {

        @DisplayName("올바른 ID로 조회할 경우, 해당 ID의 Content를 반환한다.")
        @DataSet(value = {"datasets/repository/content/base-content.yaml"})
        @Test
        void findById_successWithValidId() {
            // given
            var validId = 1L;

            // when
            var content = contentRepository.findById(validId).orElse(null);

            // then
            assertNotNull(content);
            assertEquals(validId, content.getId());
        }

        @DisplayName("올바르지 않은 ID로 조회할 경우, 빈 값을 반환한다.")
        @DataSet(value = {"datasets/repository/content/base-content.yaml"})
        @Test
        void findById_failWithInvalidId() {
            // given
            var invalidId = 999L;

            // when
            var content = contentRepository.findById(invalidId).orElse(null);

            // then
            assertNull(content);
        }
    }

    @Nested
    @DisplayName("contentRepository.save")
    class save {

        @DisplayName("올바른 Content를 저장할 경우, 성공적으로 저장된다.")
        @DataSet(value = {"datasets/repository/content/empty-content.yaml"})
        @ExpectedDataSet(value = "datasets/repository/content/saved-content.yaml")
        @Test
        void save_successWithValidContent() {
            // given
            var content = Content.builder()
                    .plainText("This is a test content.")
                    .editorText("# This is a test content.")
                    .build();

            // when
            var savedContent = contentRepository.save(content);

            // then
            assertEquals(content.getPlainText(), savedContent.getPlainText());
            assertEquals(content.getEditorText(), savedContent.getEditorText());
        }
    }

}

