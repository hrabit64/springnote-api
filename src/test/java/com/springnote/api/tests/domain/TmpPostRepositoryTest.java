package com.springnote.api.tests.domain;


import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;
import com.springnote.api.domain.tmpPost.TmpPost;
import com.springnote.api.domain.tmpPost.TmpPostRepository;
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

@DisplayName("Repository Test - TmpPost")
public class TmpPostRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private TmpPostRepository tmpPostRepository;

    @Nested
    @DisplayName("tmpPostRepository.findPostById")
    class findPostById {

        @DisplayName("주어진 id에 해당하는 임시 포스트가 존재하면, 해당 임시 포스트를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/tmpPost/base-tmpPost.yaml"
        })
        @Test
        void findPostById_success() {
            //given
            var validId = "5822d1d9-427a-4fad-99b9-7cc1deaa4271";

            //when
            var tmpPost = tmpPostRepository.findPostById(validId).orElse(null);

            //then
            assertNotNull(tmpPost);
            assertEquals(validId, tmpPost.getId());
        }

        @DisplayName("주어진 id에 해당하는 임시 포스트가 존재하지 않으면, 빈 Optional을 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/tmpPost/base-tmpPost.yaml"
        })
        @Test
        void findPostById_fail() {
            //given
            var invalidId = "invalid";

            //when
            var tmpPost = tmpPostRepository.findPostById(invalidId).orElse(null);

            //then
            assertNull(tmpPost);
        }
    }

    @Nested
    @DisplayName("tmpPostRepository.findAllBy")
    class findAllBy {
        @DisplayName("모든 임시 포스트를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/tmpPost/base-tmpPost.yaml"
        })
        @Test
        void findAllBy_success() {
            //when
            var tmpPosts = tmpPostRepository.findAllBy(PageRequest.of(0, 10));

            //then
            assertEquals(3, tmpPosts.getTotalElements());
        }

        private static Stream<Arguments> provideSorKeys() {
            return Stream.of(
                    Arguments.of("createdDate", Sort.Direction.ASC, List.of("8eba1c87-36a5-472d-a273-ab58838e1bb9", "f8eeaa95-555c-46ca-8f0a-41f34da53d26", "5822d1d9-427a-4fad-99b9-7cc1deaa4271")),
                    Arguments.of("createdDate", Sort.Direction.DESC, List.of("5822d1d9-427a-4fad-99b9-7cc1deaa4271", "f8eeaa95-555c-46ca-8f0a-41f34da53d26", "8eba1c87-36a5-472d-a273-ab58838e1bb9")),
                    Arguments.of("lastModifiedDate", Sort.Direction.ASC, List.of("5822d1d9-427a-4fad-99b9-7cc1deaa4271", "f8eeaa95-555c-46ca-8f0a-41f34da53d26", "8eba1c87-36a5-472d-a273-ab58838e1bb9")),
                    Arguments.of("lastModifiedDate", Sort.Direction.DESC, List.of("8eba1c87-36a5-472d-a273-ab58838e1bb9", "f8eeaa95-555c-46ca-8f0a-41f34da53d26", "5822d1d9-427a-4fad-99b9-7cc1deaa4271"))
            );
        }

        @DisplayName("정렬 조건에 따라 임시 포스트를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/tmpPost/base-tmpPost.yaml"
        })
        @MethodSource("provideSorKeys")
        @ParameterizedTest(name = "{index} : {0} 기준으로 {1} 정렬하면, {2} 순서로 반환된다.")
        void findAllBy_successWithSort(String sortKey, Sort.Direction direction, List<String> expectedOrder) {
            //given
            var testPageable = PageRequest.of(0, 10, Sort.by(direction, sortKey));

            //when
            var tmpPosts = tmpPostRepository.findAllBy(testPageable);

            //then
            assertEquals(expectedOrder.size(), tmpPosts.getTotalElements());
            assertTrue(ListValidator.isSameList(expectedOrder, tmpPosts.getContent().stream().map(TmpPost::getId).toList()));
        }
    }

    @Nested
    @DisplayName("tmpPostRepository.save")
    class save {
        @DisplayName("올바른 임시 포스트를 입력하면, 해당 임시 포스트를 저장한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/tmpPost/empty-tmpPost.yaml"
        })
        @ExpectedDataSet(value = "datasets/repository/tmpPost/saved-tmpPost.yaml")
        @Test
        void save_successWithValidTmpPost() {
            //given
            var tmpPost = TmpPost.builder()
                    .id("5822d1d9-427a-4fad-99b9-7cc1deaa4271")
                    .title("test")
                    .content("# This is a test content.")
                    .thumbnail("thumbnail1")
                    .series(Series.builder().id(1L).build())
                    .postType(PostType.builder().id(1L).build())
                    .build();

            //when
            tmpPostRepository.save(tmpPost);
            tmpPostRepository.flush();
        }
    }

    @Nested
    @DisplayName("tmpPostRepository.delete")
    class delete {
        @DisplayName("올바른 임시 포스트를 입력하면, 해당 임시 포스트와 TmpPostTag까지 삭제한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/tag/post-tag.yaml",
                "datasets/repository/tmpPost/base-tmpPost.yaml",
                "datasets/repository/tmpPostTag/base-tmpPostTag.yaml"

        })
        @ExpectedDataSet(value = {
                "datasets/repository/tmpPost/deleted-tmpPost.yaml",
                "datasets/repository/tmpPostTag/deleted-tmpPostTag.yaml"
        })
        @Test
        void delete_successWithValidTmpPost() {
            //given
            var validId = "5822d1d9-427a-4fad-99b9-7cc1deaa4271";
            //when
            tmpPostRepository.deleteById(validId);
            tmpPostRepository.flush();
        }
    }

}
