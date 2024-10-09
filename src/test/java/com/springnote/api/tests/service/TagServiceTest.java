package com.springnote.api.tests.service;

import com.springnote.api.domain.tag.Tag;
import com.springnote.api.domain.tag.TagRepository;
import com.springnote.api.dto.tag.common.TagResponseDto;
import com.springnote.api.dto.tag.service.TagCreateRequestServiceDto;
import com.springnote.api.dto.tag.service.TagUpdateRequestServiceDto;
import com.springnote.api.service.TagService;
import com.springnote.api.testUtils.dataFactory.TestDataFactory;
import com.springnote.api.testUtils.dataFactory.tag.TagTestDataFactory;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DisplayName("Service Test - TagService")
public class TagServiceTest extends ServiceTestTemplate {
    @InjectMocks
    private TagService tagService;

    @Mock
    private TagRepository tagRepository;


    @DisplayName("TagService.create")
    @Nested
    class create {

        @DisplayName("올바른 태그가 주어지면 태그를 생성한다.")
        @Test
        void create_success() {
            // given
            var requestDto = TagCreateRequestServiceDto.builder()
                    .name("태그")
                    .build();

            doReturn(false).when(tagRepository).existsByName(ArgumentMatchers.eq(requestDto.getName()));

            var targetTag = Tag.builder()
                    .name(requestDto.getName())
                    .build();

            var savedTag = TagTestDataFactory.createTag(requestDto.getName());

            doReturn(savedTag).when(tagRepository).save(ArgumentMatchers.eq(targetTag));
            // when
            var result = tagService.create(requestDto);

            // then
            var expected = TagResponseDto.builder()
                    .id(savedTag.getId())
                    .name(savedTag.getName())
                    .build();

            assertEquals(expected, result);
            verify(tagRepository).existsByName(ArgumentMatchers.eq(requestDto.getName()));
            verify(tagRepository).save(ArgumentMatchers.eq(targetTag));
        }

        @DisplayName("이미 존재하는 태그 이름이 주어지면 예외를 던진다.")
        @Test
        void create_fail() {
            // given
            var requestDto = TagCreateRequestServiceDto.builder()
                    .name("태그")
                    .build();

            doReturn(true).when(tagRepository).existsByName(ArgumentMatchers.eq(requestDto.getName()));

            // when
            var result = assertThrows(BusinessException.class, () -> tagService.create(requestDto));

            // then
            assertEquals(BusinessErrorCode.ITEM_ALREADY_EXIST, result.getErrorCode());
            verify(tagRepository).existsByName(ArgumentMatchers.eq(requestDto.getName()));
        }
    }

    @DisplayName("TagService.delete")
    @Nested
    class delete {

        @DisplayName("올바른 태그 id가 주어지면 태그를 삭제한다.")
        @Test
        void delete_success() {
            // given
            var targetTag = TagTestDataFactory.createTag();

            doReturn(Optional.of(targetTag)).when(tagRepository).findById(ArgumentMatchers.eq(targetTag.getId()));

            // when
            var result = tagService.delete(targetTag.getId());

            // then
            var expected = TagResponseDto.builder()
                    .id(targetTag.getId())
                    .name(targetTag.getName())
                    .build();

            assertEquals(expected, result);
            verify(tagRepository).delete(ArgumentMatchers.eq(targetTag));
            verify(tagRepository).findById(ArgumentMatchers.eq(targetTag.getId()));
        }

        @DisplayName("존재하지 않는 태그 id가 주어지면 예외를 던진다.")
        @Test
        void delete_fail() {
            // given
            var targetTag = TagTestDataFactory.createTag();

            doReturn(Optional.empty()).when(tagRepository).findById(ArgumentMatchers.eq(targetTag.getId()));

            // when
            var result = assertThrows(BusinessException.class, () -> tagService.delete(targetTag.getId()));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
        }
    }

    @DisplayName("TagService.update")
    @Nested
    class update {

        @DisplayName("올바른 태그 정보가 주어지면 태그를 수정한다.")
        @Test
        void update_success() {
            // given
            var requestDto = TagUpdateRequestServiceDto.builder()
                    .id(1L)
                    .name("수정된 태그")
                    .build();

            var targetTag = TagTestDataFactory.createTag();

            var updatedTag = Tag.builder()
                    .id(targetTag.getId())
                    .name(requestDto.getName())
                    .build();

            doReturn(Optional.of(targetTag)).when(tagRepository).findById(ArgumentMatchers.eq(targetTag.getId()));
            doReturn(false).when(tagRepository).existsByName(ArgumentMatchers.eq(requestDto.getName()));
            doReturn(updatedTag).when(tagRepository).save(ArgumentMatchers.eq(updatedTag));

            // when
            var result = tagService.update(requestDto);

            // then
            var expected = TagResponseDto.builder()
                    .id(targetTag.getId())
                    .name(requestDto.getName())
                    .build();

            assertEquals(expected, result);
            verify(tagRepository).save(ArgumentMatchers.eq(targetTag));
            verify(tagRepository).existsByName(ArgumentMatchers.eq(requestDto.getName()));
            verify(tagRepository).findById(ArgumentMatchers.eq(requestDto.getId()));
        }

        @DisplayName("수정된 태그 이름이 이미 존재하는 경우 예외를 던진다.")
        @Test
        void update_fail() {
            // given
            var requestDto = TagUpdateRequestServiceDto.builder()
                    .id(1L)
                    .name("수정된 태그")
                    .build();
            var targetTag = TagTestDataFactory.createTag();


            doReturn(Optional.of(targetTag)).when(tagRepository).findById(ArgumentMatchers.eq(targetTag.getId()));
            doReturn(true).when(tagRepository).existsByName(ArgumentMatchers.eq(requestDto.getName()));

            // when
            var result = assertThrows(BusinessException.class, () -> tagService.update(requestDto));

            // then
            assertEquals(BusinessErrorCode.ITEM_ALREADY_EXIST, result.getErrorCode());
            verify(tagRepository).existsByName(ArgumentMatchers.eq(requestDto.getName()));
            verify(tagRepository).findById(ArgumentMatchers.eq(requestDto.getId()));
        }

        @DisplayName("존재하지 않는 태그 id가 주어지면 예외를 던진다.")
        @Test
        void update_fail2() {
            // given
            var requestDto = TagUpdateRequestServiceDto.builder()
                    .id(1L)
                    .name("수정된 태그")
                    .build();

            doReturn(Optional.empty()).when(tagRepository).findById(ArgumentMatchers.eq(requestDto.getId()));

            // when
            var result = assertThrows(BusinessException.class, () -> tagService.update(requestDto));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(tagRepository).findById(ArgumentMatchers.eq(requestDto.getId()));
        }
    }

    @DisplayName("TagService.getById")
    @Nested
    class getById {

        @DisplayName("올바른 태그 id가 주어지면 태그를 반환한다.")
        @Test
        void getById_success() {
            // given
            var targetTag = TagTestDataFactory.createTag();

            doReturn(Optional.of(targetTag)).when(tagRepository).findById(ArgumentMatchers.eq(targetTag.getId()));

            // when
            var result = tagService.getById(targetTag.getId());

            // then
            var expected = TagResponseDto.builder()
                    .id(targetTag.getId())
                    .name(targetTag.getName())
                    .build();

            assertEquals(expected, result);
            verify(tagRepository).findById(ArgumentMatchers.eq(targetTag.getId()));
        }

        @DisplayName("존재하지 않는 태그 id가 주어지면 예외를 던진다.")
        @Test
        void getById_fail() {
            // given
            var targetTag = TagTestDataFactory.createTag();

            doReturn(Optional.empty()).when(tagRepository).findById(ArgumentMatchers.eq(targetTag.getId()));

            // when
            var result = assertThrows(BusinessException.class, () -> tagService.getById(targetTag.getId()));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(tagRepository).findById(ArgumentMatchers.eq(targetTag.getId()));
        }
    }

    @DisplayName("TagService.getByName")
    @Nested
    class getByName {

        @DisplayName("올바른 태그 이름이 주어지면 태그를 반환한다.")
        @Test
        void getByName_success() {
            // given
            var targetTag = TagTestDataFactory.createTag();

            doReturn(TestDataFactory.createPageObject(targetTag)).when(tagRepository).findAllByNameContaining(ArgumentMatchers.eq(targetTag.getName()), ArgumentMatchers.any(Pageable.class));

            // when
            var result = tagService.getByName(targetTag.getName(), TestDataFactory.getMockPageable());

            // then
            var expected = TagResponseDto.builder()
                    .id(targetTag.getId())
                    .name(targetTag.getName())
                    .build();

            assertEquals(1, result.getTotalElements());
            assertEquals(expected, result.getContent().get(0));
            verify(tagRepository).findAllByNameContaining(ArgumentMatchers.eq(targetTag.getName()), ArgumentMatchers.any(Pageable.class));
        }
    }

    @DisplayName("TagService.getAll")
    @Nested
    class getAll {

        @DisplayName("모든 태그를 반환한다.")
        @Test
        void getAll_success() {
            // given
            var targetTag = TagTestDataFactory.createTag();

            doReturn(TestDataFactory.createPageObject(targetTag)).when(tagRepository).findAll(any(Pageable.class));

            // when
            var result = tagService.getAll(TestDataFactory.getMockPageable());

            // then
            var expected = TagResponseDto.builder()
                    .id(targetTag.getId())
                    .name(targetTag.getName())
                    .build();

            assertEquals(1, result.getTotalElements());
            assertEquals(expected, result.getContent().get(0));
            verify(tagRepository).findAll(ArgumentMatchers.any(Pageable.class));
        }
    }

    @DisplayName("TagService.isExistByName")
    @Nested
    class isExistByName {

        @DisplayName("존재하는 태그 이름이 주어지면 true를 반환한다.")
        @Test
        void isExistByName_success() {
            // given
            var targetTag = TagTestDataFactory.createTag();

            doReturn(true).when(tagRepository).existsByName(ArgumentMatchers.eq(targetTag.getName()));

            // when
            var result = tagService.isExistByName(targetTag.getName());

            // then
            assertTrue(result);
            verify(tagRepository).existsByName(ArgumentMatchers.eq(targetTag.getName()));
        }

        @DisplayName("존재하지 않는 태그 이름이 주어지면 false를 반환한다.")
        @Test
        void isExistByName_fail() {
            // given
            var targetTag = TagTestDataFactory.createTag();

            doReturn(false).when(tagRepository).existsByName(ArgumentMatchers.eq(targetTag.getName()));

            // when
            var result = tagService.isExistByName(targetTag.getName());

            // then
            assertFalse(result);
            verify(tagRepository).existsByName(ArgumentMatchers.eq(targetTag.getName()));
        }
    }
}
