package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.postType.PostTypeRepository;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@DisplayName("Repository Test - PostType")
public class PostTypeRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private PostTypeRepository postTypeRepository;

    @Nested
    @DisplayName("postTypeRepository.findAll")
    class findAll {

        @DisplayName("모든 PostType을 조회한다.")
        @DataSet(value = "datasets/repository/postType/base-postType.yaml")
        @Test
        void findAll_success() {
            // when
            var postTypes = postTypeRepository.findAll();

            // then
            assertEquals(postTypes.size(), 4);
        }

        private static Stream<Arguments> provideSortKey() throws Throwable {
            return Stream.of(

                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L, 4L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(4L, 3L, 2L, 1L)),
                    Arguments.of("name", Sort.Direction.ASC, List.of(4L, 2L, 1L, 3L)),
                    Arguments.of("name", Sort.Direction.DESC, List.of(3L, 1L, 2L, 4L))
            );
        }

        @DisplayName("정렬 조건이 주어지면, 해당 조건에 맞게 PostType을 조회한다.")
        @DataSet(value = "datasets/repository/postType/base-postType.yaml")
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "{index} : {0} 을 기준으로 {1} 순으로 조회 하면, {2} 순으로 조회된다.")
        void findAll_sort_success(String sortKey, Sort.Direction direction, List<Long> expected) {
            // given
            var testPageable = PageRequest.of(0, 10, Sort.by(direction, sortKey));

            // when
            var result = postTypeRepository.findAll(testPageable);

            // then
            assertTrue(ListValidator.isSameList(result.stream().map(PostType::getId).toList(), expected));
        }
    }

}
