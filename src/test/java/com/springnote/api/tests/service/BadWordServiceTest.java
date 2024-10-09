package com.springnote.api.tests.service;

import com.springnote.api.domain.badWord.BadWord;
import com.springnote.api.domain.badWord.BadWordRepository;
import com.springnote.api.dto.badWord.common.BadWordResponseCommonDto;
import com.springnote.api.dto.badWord.service.BadWordCreateRequestServiceDto;
import com.springnote.api.service.BadWordService;
import com.springnote.api.testUtils.dataFactory.TestDataFactory;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static com.springnote.api.testUtils.dataFactory.badWord.BadWordTestDataFactory.createStandardBadWord;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DisplayName("Service Test - BadWordService")
public class BadWordServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private BadWordService badWordService;

    @Mock
    private BadWordRepository badWordRepository;

    @Nested
    @DisplayName("BadWordService.getAll")
    class getAll {

        @DisplayName("type가 null이 아닐 때, type에 따라 비속어 목록을 조회한다.")
        @Test
        void getAll_successWithType() {
            // given
            var returnBadWord = createStandardBadWord(true);

            doReturn(new PageImpl<>(List.of(returnBadWord)))
                    .when(badWordRepository)
                    .findBadWordByType(ArgumentMatchers.eq(true), ArgumentMatchers.any(Pageable.class));
            // when
            var result = badWordService.getAll(TestDataFactory.getMockPageable(), true);

            // then
            var expected = BadWordResponseCommonDto
                    .builder()
                    .id(1L)
                    .word(returnBadWord.getWord())
                    .isBadWord(true)
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(badWordRepository).findBadWordByType(ArgumentMatchers.eq(true), ArgumentMatchers.any(Pageable.class));

        }

        @DisplayName("type가 null일 때, 모든 비속어 목록을 조회한다.")
        @Test
        void getAll_successWithoutType() {
            // given
            var returnBadWord = createStandardBadWord(true);

            doReturn(new PageImpl<>(List.of(returnBadWord)))
                    .when(badWordRepository)
                    .findAll(ArgumentMatchers.any(Pageable.class));
            // when
            var result = badWordService.getAll(TestDataFactory.getMockPageable(), null);

            // then
            var expected = BadWordResponseCommonDto
                    .builder()
                    .id(1L)
                    .word(returnBadWord.getWord())
                    .isBadWord(true)
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(badWordRepository).findAll(ArgumentMatchers.any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("BadWordService.getAllByWord")
    class getAllByWord {

        @DisplayName("word와 type에 따라 비속어 목록을 조회한다.")
        @Test
        void getAllByWord_success() {
            // given
            var returnBadWord = createStandardBadWord(true);

            doReturn(new PageImpl<>(List.of(returnBadWord)))
                    .when(badWordRepository)
                    .matchByWord(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(true), ArgumentMatchers.any(Pageable.class));
            // when
            var result = badWordService.getAllByWord("1", true, TestDataFactory.getMockPageable());

            // then
            var expected = BadWordResponseCommonDto
                    .builder()
                    .id(1L)
                    .word(returnBadWord.getWord())
                    .isBadWord(true)
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(badWordRepository).matchByWord(ArgumentMatchers.eq("1"), ArgumentMatchers.eq(true), ArgumentMatchers.any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("BadWordService.getById")
    class getById {

        @DisplayName("id에 따라 비속어를 조회한다.")
        @Test
        void getById_success() {
            // given
            var returnBadWord = createStandardBadWord(true);

            doReturn(Optional.of(returnBadWord))
                    .when(badWordRepository)
                    .findById(ArgumentMatchers.eq(1L));
            // when
            var result = badWordService.getById(1L);

            // then
            var expected = BadWordResponseCommonDto
                    .builder()
                    .id(1L)
                    .word(returnBadWord.getWord())
                    .isBadWord(true)
                    .build();

            assertEquals(expected, result);
            verify(badWordRepository).findById(ArgumentMatchers.eq(1L));
        }

        @DisplayName("존재하지 않는 비속어를 조회하려고 할 때, BusinessException이 발생한다.")
        @Test
        void getById_fail() {
            // given
            doReturn(Optional.empty())
                    .when(badWordRepository)
                    .findById(ArgumentMatchers.eq(1L));
            // when
            var result = assertThrows(BusinessException.class, () -> badWordService.getById(1L));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(badWordRepository).findById(ArgumentMatchers.eq(1L));
        }
    }

    @Nested
    @DisplayName("BadWordService.create")
    class create {

        @DisplayName("새로운 비속어를 생성한다.")
        @Test
        void create_success() {
            // given
            var requestDto = BadWordCreateRequestServiceDto
                    .builder()
                    .word("1")
                    .isBadWord(true)
                    .build();

            var returnBadWord = BadWord
                    .builder()
                    .id(1L)
                    .word("1")
                    .type(true)
                    .build();

            doReturn(false)
                    .when(badWordRepository)
                    .existsByWord(ArgumentMatchers.eq("1"));

            doReturn(returnBadWord)
                    .when(badWordRepository)
                    .save(
                            BadWord.builder()
                                    .id(null)
                                    .word("1")
                                    .type(true)
                                    .build()
                    );

            // when
            var result = badWordService.create(requestDto);

            // then
            var expected = BadWordResponseCommonDto
                    .builder()
                    .id(1L)
                    .word("1")
                    .isBadWord(true)
                    .build();

            assertEquals(expected, result);
            verify(badWordRepository).existsByWord(ArgumentMatchers.eq("1"));
            verify(badWordRepository).save(ArgumentMatchers.any(BadWord.class));
        }

        @DisplayName("이미 존재하는 비속어를 생성하려고 할 때, BusinessException이 발생한다.")
        @Test
        void create_fail() {
            // given
            var requestDto = BadWordCreateRequestServiceDto
                    .builder()
                    .word("1")
                    .isBadWord(true)
                    .build();

            doReturn(true)
                    .when(badWordRepository)
                    .existsByWord(ArgumentMatchers.eq("1"));

            // when
            // then
            assertThrows(BusinessException.class, () -> badWordService.create(requestDto));
            verify(badWordRepository).existsByWord(ArgumentMatchers.eq("1"));
        }
    }

    @Nested
    @DisplayName("BadWordService.delete")
    class delete {

        @DisplayName("id에 따라 비속어를 삭제한다.")
        @Test
        void delete_success() {
            // given
            var returnBadWord = BadWord
                    .builder()
                    .id(1L)
                    .word("1")
                    .type(true)
                    .build();

            doReturn(Optional.of(returnBadWord))
                    .when(badWordRepository)
                    .findById(ArgumentMatchers.eq(1L));
            // when
            var result = badWordService.delete(1L);

            // then
            var expected = BadWordResponseCommonDto
                    .builder()
                    .id(1L)
                    .word("1")
                    .isBadWord(true)
                    .build();

            assertEquals(expected, result);
            verify(badWordRepository).delete(ArgumentMatchers.any(BadWord.class));
        }

        @DisplayName("존재하지 않는 비속어를 삭제하려고 할 때, BusinessException이 발생한다.")
        @Test
        void delete_fail() {
            // given
            doReturn(Optional.empty())
                    .when(badWordRepository)
                    .findById(ArgumentMatchers.eq(1L));
            // when
            // then
            assertThrows(BusinessException.class, () -> badWordService.delete(1L));
            verify(badWordRepository).findById(ArgumentMatchers.eq(1L));
        }
    }

}
