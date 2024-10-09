package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.series.Series;
import com.springnote.api.domain.series.SeriesRepository;
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

@DisplayName("Repository Test - Series")
public class SeriesRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private SeriesRepository seriesRepository;

    @Nested
    @DisplayName("seriesRepository.findById")
    class findById {

        @DisplayName("올바른 Series 의 Id 를 입력하면 Series 를 반환한다.")
        @DataSet(value = "datasets/repository/series/base-series.yaml")
        @Test
        void findById_successWithValidId() {
            // given
            var validId = 1L;

            // when
            var series = seriesRepository.findById(validId).orElse(null);

            // then
            assertNotNull(series);
            assertEquals(validId, series.getId());
        }

        @DisplayName("올바르지 않은 Series 의 Id 를 입력하면 Series 를 반환하지 않는다.")
        @DataSet(value = "datasets/repository/series/base-series.yaml")
        @Test
        void findById_failWithInvalidId() {
            // given
            var invalidId = 999L;

            // when
            var series = seriesRepository.findById(invalidId).orElse(null);

            // then
            assertNull(series);
        }
    }

    @Nested
    @DisplayName("seriesRepository.existsByName")
    class existsByName {

        @DisplayName("존재하는 Series 의 이름을 입력하면 true 를 반환한다.")
        @DataSet(value = "datasets/repository/series/base-series.yaml")
        @Test
        void existsByName_successWithValidName() {
            // given
            var validName = "전역하고싶다.";

            // when
            var result = seriesRepository.existsByName(validName);

            // then
            assertTrue(result);
        }

        @DisplayName("존재하지 않는 Series 의 이름을 입력하면 false 를 반환한다.")
        @DataSet(value = "datasets/repository/series/base-series.yaml")
        @Test
        void existsByName_failWithInvalidName() {
            // given
            var invalidName = "invalid";

            // when
            var result = seriesRepository.existsByName(invalidName);

            // then
            assertFalse(result);
        }
    }

    @Nested
    @DisplayName("seriesRepository.save")
    class save {

        @DisplayName("올바른 Series 를 입력하면 Series 를 저장한다.")
        @DataSet(value = "datasets/repository/series/empty-series.yaml")
        @ExpectedDataSet(value = "datasets/repository/series/saved-series.yaml")
        @Test
        void save_success() {
            // given
            var series = Series.builder()
                    .name("전역")
                    .description("저는 전역이 하고 싶어요")
                    .thumbnail("https://test.springnote.blog")
                    .build();

            // when
            var savedSeries = seriesRepository.save(series);
            seriesRepository.flush();

        }
    }

    @Nested
    @DisplayName("seriesRepository.findAllByNameContaining")
    class findAllByNameContaining {

        @DisplayName("포함하는 이름을 입력하면 해당 이름을 포함하는 Series 를 반환한다.")
        @DataSet(value = "datasets/repository/series/base-series.yaml")
        @Test
        void findAllByNameContaining_successWithValidName() {
            // given
            var validName = "전역";

            // when
            var seriesList = seriesRepository.findAllByNameContaining(validName, PageRequest.of(0, 10));

            // then
            assertNotNull(seriesList);
            assertEquals(1, seriesList.getTotalElements());
        }

        @DisplayName("포함하지 않는 이름을 입력하면 해당 이름을 포함하는 Series 를 반환하지 않는다.")
        @DataSet(value = "datasets/repository/series/base-series.yaml")
        @Test
        void findAllByNameContaining_failWithInvalidName() {
            // given
            var invalidName = "invalid";

            // when
            var seriesList = seriesRepository.findAllByNameContaining(invalidName, PageRequest.of(0, 10));

            // then
            assertNotNull(seriesList);
            assertEquals(0, seriesList.getTotalElements());
        }

        private static Stream<Arguments> provideSortKey() throws Throwable {
            return Stream.of(

                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(2L, 1L)),
                    Arguments.of("name", Sort.Direction.ASC, List.of(2L, 1L)),
                    Arguments.of("name", Sort.Direction.DESC, List.of(1L, 2L))
            );
        }

        @DisplayName("포함하는 이름을 입력하고 정렬 키를 입력하면 해당 이름을 포함하는 Series 를 정렬하여 반환한다.")
        @DataSet(value = "datasets/repository/series/base-series.yaml")
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "정렬 키가 {0} 이고, 정렬 방향이 {1} 일 때, {2} 순서로 조회된다.")
        void findAllByNameContaining_successWithSortKey(String sortKey, Sort.Direction direction, List<Long> expectedIds) {
            // given
            var name = "하고";
            var testPageable = PageRequest.of(0, 10, Sort.by(direction, sortKey));

            // when
            var result = seriesRepository.findAllByNameContaining(name, testPageable);

            // then
            assertEquals(2, result.getTotalElements());
            assertTrue(ListValidator.isSameList(result.stream().map(Series::getId).toList(), expectedIds));
        }


    }

}
