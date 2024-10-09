package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.tag.Tag;
import com.springnote.api.domain.tag.TagRepository;
import com.springnote.api.testUtils.template.RepositoryTestTemplate;
import com.springnote.api.testUtils.validator.ListValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Repository Test - Tag")
public class TagRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private TagRepository tagRepository;


    @DisplayName("tagRepository.findTagsByIdIn")
    @Nested
    class findTagsByIdIn {

        @DisplayName("주어진 id들 에 해당하는 Tag 들을 조회한다.")
        @DataSet(value = "datasets/repository/tag/base-tag.yaml")
        @Test
        void findTagsByIdIn_success() {
            // given
            var tagIds = List.of(1L, 2L);

            // when
            var result = tagRepository.findTagsByIdIn(tagIds);

            // then
            assertEquals(tagIds.size(), result.size());
            assertTrue(ListValidator.isSameList(tagIds, result.stream().map(Tag::getId).toList()));
        }

        @DisplayName("일부만 존재하는 Id들이 주어지면, 해당하는 Tag들만 조회한다.")
        @DataSet(value = "datasets/repository/tag/base-tag.yaml")
        @Test
        void findTagsByIdIn_successPortion() {
            // given
            var validTagId = 1L;
            var invalidTagId = 999L;
            var tagIds = List.of(validTagId, invalidTagId);

            // when
            var result = tagRepository.findTagsByIdIn(tagIds);

            // then
            assertEquals(1, result.size());
            assertEquals(validTagId, result.get(0).getId());
        }

        @DisplayName("모두 존재하지 않는 Id들이 주어지면, 빈 리스트를 반환한다.")
        @DataSet(value = "datasets/repository/tag/base-tag.yaml")
        @Test
        void findTagsByIdIn_fail() {
            // given

            var invalidTagIds = List.of(999L, 888L);

            // when
            var result = tagRepository.findTagsByIdIn(invalidTagIds);

            // then
            assertEquals(0, result.size());
        }

    }

    @DisplayName("tagRepository.existsByName")
    @Nested
    class existsByName {

        @DisplayName("주어진 이름을 가진 Tag가 존재하면, true를 반환한다.")
        @DataSet(value = "datasets/repository/tag/base-tag.yaml")
        @Test
        void existsByName_success() {
            // given
            var existsTagName = "2-tag";

            // when
            var result = tagRepository.existsByName(existsTagName);

            // then
            assertTrue(result);
        }

        @DisplayName("주어진 이름을 가진 Tag가 존재하지 않으면, false를 반환한다.")
        @DataSet(value = "datasets/repository/tag/base-tag.yaml")
        @Test
        void existsByName_fail() {
            // given
            var notExistsTagName = "전역하고싶다.";

            // when
            var result = tagRepository.existsByName(notExistsTagName);

            // then
            assertFalse(result);
        }

    }

    @DisplayName("tagRepository.findAllByNameContaining")
    @Nested
    class findAllByNameContaining {


        @DisplayName("주어진 이름을 포함하는 Tag 들을 조회한다.")
        @DataSet(value = "datasets/repository/tag/base-tag.yaml")
        @Test
        void findAllByNameContaining_success() {
            // given
            var name = "tag";

            // when
            var result = tagRepository.findAllByNameContaining(name, null);

            // then
            assertEquals(2, result.getTotalElements());
        }

        private static Stream<Arguments> provideSortKey() throws Throwable {
            return Stream.of(

                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(2L, 1L)),
                    Arguments.of("name", Sort.Direction.ASC, List.of(2L, 1L)),
                    Arguments.of("name", Sort.Direction.DESC, List.of(1L, 2L))
            );
        }

        @DisplayName("주어진 이름을 포함하는 Tag들을 정렬하여 조회한다.")
        @DataSet(value = "datasets/repository/tag/base-tag.yaml")
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "정렬 키가 {0} 이고, 정렬 방향이 {1} 일 때, {2} 순서로 조회된다.")
        void findAllByNameContaining_withSort(String sortKey, Sort.Direction direction, List<Long> expected) {
            // given
            var name = "tag";
            var testPageable = PageRequest.of(0, 10, Sort.by(direction, sortKey));

            // when
            var result = tagRepository.findAllByNameContaining(name, testPageable);

            // then
            assertEquals(2, result.getTotalElements());
            assertTrue(ListValidator.isSameList(result.stream().map(Tag::getId).toList(), expected));
        }

    }

    @DisplayName("tagRepository.findById")
    @Nested
    class findById {

        @DisplayName("올바른 ID로 조회할 경우, 해당 ID의 Tag를 반환한다.")
        @DataSet(value = "datasets/repository/tag/base-tag.yaml")
        @Test
        void findById_successWithValidId() {
            // given
            var validId = 1L;

            // when
            var tag = tagRepository.findById(validId).orElse(null);

            // then
            assertNotNull(tag);
            assertEquals(validId, tag.getId());
        }

        @DisplayName("올바르지 않은 ID로 조회할 경우, 빈 값을 반환한다.")
        @DataSet(value = "datasets/repository/tag/base-tag.yaml")
        @Test
        void findById_failWithInvalidId() {
            // given
            var invalidId = 999L;

            // when
            var tag = tagRepository.findById(invalidId).orElse(null);

            // then
            assertNull(tag);
        }
    }

    @DisplayName("tagRepository.save")
    @Nested
    class save {

        @DisplayName("올바른 Tag를 저장할 경우, 성공적으로 저장된다.")
        @DataSet(value = "datasets/repository/tag/empty-tag.yaml")
        @ExpectedDataSet(value = "datasets/repository/tag/saved-tag.yaml")
        @Test
        void save_successWithValidTag() {
            // given
            var tag = Tag.builder()
                    .name("tag")
                    .build();

            // when
            var savedTag = tagRepository.save(tag);
            tagRepository.flush();

            // then
            assertEquals(tag.getName(), savedTag.getName());
        }
    }

}

