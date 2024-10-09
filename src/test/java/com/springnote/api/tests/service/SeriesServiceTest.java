package com.springnote.api.tests.service;

import com.springnote.api.domain.post.PostRepository;
import com.springnote.api.domain.series.Series;
import com.springnote.api.domain.series.SeriesRepository;
import com.springnote.api.dto.series.common.SeriesResponseCommonDto;
import com.springnote.api.dto.series.service.SeriesCreateRequestServiceDto;
import com.springnote.api.dto.series.service.SeriesUpdateRequestServiceDto;
import com.springnote.api.service.SeriesService;
import com.springnote.api.testUtils.dataFactory.TestDataFactory;
import com.springnote.api.testUtils.dataFactory.post.PostTestDataFactory;
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

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DisplayName("Service Test - SeriesService")
public class SeriesServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private SeriesService seriesService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @DisplayName("seriesService.create")
    @Nested
    class create {

        @DisplayName("정상적인 시리즈가 주어지면 시리즈를 생성한다.")
        @Test
        void create_success() {
            // given
            var requestDto = SeriesCreateRequestServiceDto.builder()
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            var targetSeries = Series.builder()
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            var savedSeries = Series.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            doReturn(false).when(seriesRepository).existsByName(requestDto.getName());
            doReturn(savedSeries).when(seriesRepository).save(targetSeries);

            // when
            var result = seriesService.create(requestDto);

            // then
            var expected = SeriesResponseCommonDto.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            assertEquals(expected, result);
            verify(seriesRepository).existsByName(requestDto.getName());
            verify(seriesRepository).save(targetSeries);
        }

        @DisplayName("중복된 이름의 시리즈가 주어지면 예외를 던진다.")
        @Test
        void create_fail() {
            // given
            var requestDto = SeriesCreateRequestServiceDto.builder()
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            doReturn(true).when(seriesRepository).existsByName(requestDto.getName());

            // when
            var result = assertThrows(BusinessException.class, () -> seriesService.create(requestDto));

            // then
            assertEquals(BusinessErrorCode.ITEM_ALREADY_EXIST, result.getErrorCode());
            verify(seriesRepository).existsByName(requestDto.getName());
        }
    }

    @DisplayName("seriesService.delete")
    @Nested
    class delete {

        @DisplayName("시리즈 id가 주어지면 시리즈를 삭제한다.")
        @Test
        void delete_success() {
            // given
            var targetSeries = Series.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            var targetPost = PostTestDataFactory.createPost();
            doReturn(Optional.of(targetSeries)).when(seriesRepository).findById(1L);
            doReturn(List.of(targetPost)).when(postRepository).findAllBySeries(targetSeries);

            // when
            var result = seriesService.delete(1L);

            // then
            var expected = SeriesResponseCommonDto.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            assertEquals(expected, result);
            verify(seriesRepository).findById(1L);
            verify(seriesRepository).delete(targetSeries);
            verify(postRepository).findAllBySeries(targetSeries);
            verify(postRepository).deleteAll(List.of(targetPost));
        }

        @DisplayName("존재하지 않는 시리즈 id가 주어지면 예외를 던진다.")
        @Test
        void delete_fail() {
            // given
            doReturn(Optional.empty()).when(seriesRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> seriesService.delete(1L));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(seriesRepository).findById(1L);
        }
    }

    @DisplayName("seriesService.update")
    @Nested
    class update {

        @DisplayName("정상적인 요청이 주어지면 시리즈를 수정한다.")
        @Test
        void update_success() {
            // given
            var requestDto = SeriesUpdateRequestServiceDto.builder()
                    .id(1L)
                    .name("수정된 시리즈 이름")
                    .description("수정된 시리즈 설명")
                    .thumbnail("수정된 시리즈 썸네일")
                    .build();

            var targetSeries = Series.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            var updatedSeries = Series.builder()
                    .id(1L)
                    .name("수정된 시리즈 이름")
                    .description("수정된 시리즈 설명")
                    .thumbnail("수정된 시리즈 썸네일")
                    .build();

            doReturn(Optional.of(targetSeries)).when(seriesRepository).findById(1L);
            doReturn(false).when(seriesRepository).existsByName(requestDto.getName());
            doReturn(updatedSeries).when(seriesRepository).save(targetSeries);

            // when
            var result = seriesService.update(requestDto);

            // then
            var expected = SeriesResponseCommonDto.builder()
                    .id(1L)
                    .name("수정된 시리즈 이름")
                    .description("수정된 시리즈 설명")
                    .thumbnail("수정된 시리즈 썸네일")
                    .build();

            assertEquals(expected, result);
            verify(seriesRepository).findById(1L);
            verify(seriesRepository).existsByName(requestDto.getName());
            verify(seriesRepository).save(targetSeries);
        }

        @DisplayName("존재하지 않는 시리즈 id가 주어지면 예외를 던진다.")
        @Test
        void update_failWithNotExists() {
            // given
            var requestDto = SeriesUpdateRequestServiceDto.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            doReturn(Optional.empty()).when(seriesRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> seriesService.update(requestDto));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(seriesRepository).findById(1L);
        }

        @DisplayName("중복된 이름의 시리즈가 주어지면, 예외를 던진다.")
        @Test
        void update_failWithExistsName() {
            // given
            var requestDto = SeriesUpdateRequestServiceDto.builder()
                    .id(1L)
                    .name("중복 시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            var targetSeries = Series.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            doReturn(Optional.of(targetSeries)).when(seriesRepository).findById(1L);
            doReturn(true).when(seriesRepository).existsByName(requestDto.getName());

            // when
            var result = assertThrows(BusinessException.class, () -> seriesService.update(requestDto));

            // then
            assertEquals(BusinessErrorCode.ITEM_ALREADY_EXIST, result.getErrorCode());
            verify(seriesRepository).findById(1L);
            verify(seriesRepository).existsByName(requestDto.getName());
        }

        @DisplayName("시리즈 이름이 변경되지 않으면, 이름을 제외하고 시리즈를 수정한다.")
        @Test
        void update_successWithoutName() {
            // given
            var requestDto = SeriesUpdateRequestServiceDto.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            var targetSeries = Series.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            var updatedSeries = Series.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("수정된 시리즈 설명")
                    .thumbnail("수정된 시리즈 썸네일")
                    .build();

            doReturn(Optional.of(targetSeries)).when(seriesRepository).findById(1L);
            doReturn(updatedSeries).when(seriesRepository).save(targetSeries);

            // when
            var result = seriesService.update(requestDto);

            // then
            var expected = SeriesResponseCommonDto.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("수정된 시리즈 설명")
                    .thumbnail("수정된 시리즈 썸네일")
                    .build();

            assertEquals(expected, result);
            verify(seriesRepository).findById(1L);
            verify(seriesRepository).save(targetSeries);
        }
    }

    @DisplayName("seriesService.get")
    @Nested
    class getSeriesById {

        @DisplayName("시리즈 id가 주어지면 시리즈를 반환한다.")
        @Test
        void getSeriesById_success() {
            // given
            var targetSeries = Series.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            doReturn(Optional.of(targetSeries)).when(seriesRepository).findById(1L);

            // when
            var result = seriesService.getSeriesById(1L);

            // then
            var expected = SeriesResponseCommonDto.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            assertEquals(expected, result);
            verify(seriesRepository).findById(1L);
        }

        @DisplayName("존재하지 않는 시리즈 id가 주어지면 예외를 던진다.")
        @Test
        void getSeriesById_fail() {
            // given
            doReturn(Optional.empty()).when(seriesRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> seriesService.getSeriesById(1L));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(seriesRepository).findById(1L);
        }
    }

    @DisplayName("seriesService.getSeries")
    @Nested
    class getSeries {

        @DisplayName("페이징 정보가 주어지면 시리즈 목록을 반환한다.")
        @Test
        void getSeries_success() {
            // given
            var targetSeries = Series.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            doReturn(TestDataFactory.createPageObject(targetSeries))
                    .when(seriesRepository)
                    .findAll(ArgumentMatchers.any(Pageable.class));

            // when
            var result = seriesService.getSeries(TestDataFactory.getMockPageable());

            // then
            var expected = SeriesResponseCommonDto.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(seriesRepository).findAll(ArgumentMatchers.any(Pageable.class));
        }
    }

    @DisplayName("seriesService.getSeriesByName")
    @Nested
    class getSeriesByName {

        @DisplayName("이름이 주어지면 해당 이름을 포함하는 시리즈 목록을 반환한다.")
        @Test
        void getSeriesByName_success() {
            // given
            var targetSeries = Series.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            doReturn(TestDataFactory.createPageObject(targetSeries))
                    .when(seriesRepository)
                    .findAllByNameContaining(ArgumentMatchers.eq("시리즈 이름"), ArgumentMatchers.any(Pageable.class));

            // when
            var result = seriesService.getSeriesByName("시리즈 이름", TestDataFactory.getMockPageable());

            // then
            var expected = SeriesResponseCommonDto.builder()
                    .id(1L)
                    .name("시리즈 이름")
                    .description("시리즈 설명")
                    .thumbnail("시리즈 썸네일")
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(seriesRepository).findAllByNameContaining(ArgumentMatchers.eq("시리즈 이름"), ArgumentMatchers.any(Pageable.class));
        }
    }

    @DisplayName("seriesService.isNameExist")
    @Nested
    class isNameExist {

        @DisplayName("이름이 주어지면 해당 이름을 포함하는 시리즈가 존재하는지 확인한다.")
        @Test
        void isNameExist_success() {
            // given
            doReturn(false).when(seriesRepository).existsByName("시리즈 이름");

            // when
            var result = seriesService.isNameExist("시리즈 이름");

            // then
            assertTrue(result);
            verify(seriesRepository).existsByName("시리즈 이름");
        }
    }
}
