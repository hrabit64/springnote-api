package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.content.Content;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.post.PostQueryKeys;
import com.springnote.api.domain.post.PostRepository;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;
import com.springnote.api.testUtils.combination.CombinationGenerator;
import com.springnote.api.testUtils.combination.KeyValueItem;
import com.springnote.api.testUtils.template.RepositoryTestTemplate;
import com.springnote.api.testUtils.validator.ListValidator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DisplayName("Repository Test - Post")
public class PostRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private PostRepository postRepository;

    @Nested
    @DisplayName("postRepository.findById")
    class findById {

        @DisplayName("올바른 Post 의 Id 를 입력하면 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"}
        )
        @Test
        void findById_successWithValidId() {
            // given
            var validId = 1L;

            // when
            var post = postRepository.findById(validId).orElse(null);

            // then
            assertNotNull(post);
            assertEquals(validId, post.getId());
        }

        @DisplayName("존재하지 않는 Post 의 Id 를 입력하면 Post 를 반환하지 않는다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void findById_failWithInvalidId() {
            // given
            var invalidId = 100L;

            // when
            var post = postRepository.findById(invalidId).orElse(null);

            // then
            assertNull(post);

        }

        @Nested
        @DisplayName("postRepository.existsByTitle")
        class existsByTitle {

            @DisplayName("존재하는 Post 의 Title 을 입력하면 true 를 반환한다.")
            @DataSet(value = {
                    "datasets/repository/postType/base-postType.yaml",
                    "datasets/repository/series/base-series.yaml",
                    "datasets/repository/content/base-content.yaml",
                    "datasets/repository/post/base-post.yaml"
            })
            @Test
            void existsByTitle_successWithValidTitle() {
                // given
                var validTitle = "test";

                // when
                var exists = postRepository.existsByTitle(validTitle);

                // then
                assertTrue(exists);
            }

            @DisplayName("존재하지 않는 Post 의 Title 을 입력하면 false 를 반환한다.")
            @DataSet(value = {
                    "datasets/repository/postType/base-postType.yaml",
                    "datasets/repository/series/base-series.yaml",
                    "datasets/repository/content/base-content.yaml",
                    "datasets/repository/post/base-post.yaml"
            })
            @Test
            void existsByTitle_failWithInvalidTitle() {
                // given
                var invalidTitle = "invalid";

                // when
                var exists = postRepository.existsByTitle(invalidTitle);

                // then
                assertFalse(exists);
            }

        }

    }

    @Nested
    @DisplayName("postRepository.matchByTitle")
    class matchByTitle {

        @DisplayName("제목으로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void matchByTitle_success() {
            // given
            var title = "corn";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByTitle(title, emptyQueryParams, testPageable);

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getTitle().contains(title));
        }

        @DisplayName("제목으로 매칭되는 Post 가 없으면 빈 페이지를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void matchByTitle_fail() {
            // given
            var title = "invalid";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByTitle(title, emptyQueryParams, testPageable);

            // then
            assertEquals(0, result.getTotalElements());
        }

        private static Stream<Arguments> provideQueryParam() throws Throwable {

            var querySet = new HashSet<KeyValueItem<PostQueryKeys>>();

            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.POST_TYPE, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.SERIES, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.TAG, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.IS_ONLY_OPEN_POST, "true"));

            var allCombinations = CombinationGenerator.generate(querySet);

            return allCombinations.stream().map(Arguments::of);
        }

        @DisplayName("제목과 주어진 쿼리 파라미터로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/tag/post-tag.yaml",
                "datasets/repository/post/title-match-queryparam-post.yaml",
                "datasets/repository/postTag/base-postTag.yaml"
        })
        @MethodSource("provideQueryParam")
        @ParameterizedTest(name = "{index} : {0}")
        void matchByTitle_successWithQueryParam(List<KeyValueItem<PostQueryKeys>> queryParam) {
            // given
            var title = "corn";

            var queryParams = new LinkedMultiValueMap<PostQueryKeys, String>();
            queryParam.forEach(item -> queryParams.add(item.key(), item.value()));

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByTitle(title, queryParams, testPageable);

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getTitle().contains(title));
            assertTrue(validateWithQueryKey(result.getContent().get(0), queryParams));
        }

        private static Stream<Arguments> provideSortKey() {
            return Stream.of(
                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("title", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("title", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("content", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("content", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("isOpen", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("isOpen", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("series", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("series", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("createdDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("createdDate", Sort.Direction.DESC, List.of(1L, 2L, 3L))
            );
        }

        @DisplayName("제목으로 매칭되는 Post 를 정렬하여 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/sort-post.yaml"
        })
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "{index} : 정렬 키가 {0} 이고, 정렬 방향이 {1} 일 때, {2} 순서로 조회된다.")
        void matchByTitle_withSort(String sortKey, Sort.Direction direction, List<Long> expected) {
            // given
            var title = "corn";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10, Sort.by(direction, sortKey));
            // when
            var result = postRepository.matchByTitle(title, emptyQueryParams, testPageable);

            // then
            assertEquals(3, result.getTotalElements());
            assertTrue(ListValidator.isSameList(result.stream().map(Post::getId).toList(), expected));
        }
    }

    @Nested
    @DisplayName("postRepository.matchByContent")
    class matchByContent {
        @DisplayName("내용으로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void matchByContent_success() {
            // given
            var content = "corn";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByContent(content, emptyQueryParams, testPageable);

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getContent().getPlainText().contains(content));
        }

        @DisplayName("내용으로 매칭되는 Post 가 없으면 빈 페이지를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void matchByContent_fail() {
            // given
            var content = "invalid";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByContent(content, emptyQueryParams, testPageable);

            // then
            assertEquals(0, result.getTotalElements());
        }

        private static Stream<Arguments> provideQueryParam() throws Throwable {

            var querySet = new HashSet<KeyValueItem<PostQueryKeys>>();

            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.POST_TYPE, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.SERIES, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.TAG, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.IS_ONLY_OPEN_POST, "true"));

            var allCombinations = CombinationGenerator.generate(querySet);

            return allCombinations.stream().map(Arguments::of);
        }

        @DisplayName("본문과 주어진 쿼리 파라미터로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/content-match-content.yaml",
                "datasets/repository/tag/post-tag.yaml",
                "datasets/repository/post/title-match-queryparam-post.yaml",
                "datasets/repository/postTag/base-postTag.yaml"
        })
        @MethodSource("provideQueryParam")
        @ParameterizedTest(name = "{index} : {0}")
        void matchByContent_successWithQueryParam(List<KeyValueItem<PostQueryKeys>> queryParam) {
            // given
            var title = "corn";

            var queryParams = new LinkedMultiValueMap<PostQueryKeys, String>();
            queryParam.forEach(item -> queryParams.add(item.key(), item.value()));

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByContent(title, queryParams, testPageable);

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getTitle().contains(title));
            assertTrue(validateWithQueryKey(result.getContent().get(0), queryParams));
        }

        private static Stream<Arguments> provideSortKey() {
            return Stream.of(
                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("title", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("title", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("content", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("content", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("isOpen", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("isOpen", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("series", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("series", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("createdDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("createdDate", Sort.Direction.DESC, List.of(1L, 2L, 3L))
            );
        }

        @DisplayName("본문으로 매칭되는 Post 를 정렬하여 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/sort-post.yaml"
        })
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "{index} : 정렬 키가 {0} 이고, 정렬 방향이 {1} 일 때, {2} 순서로 조회된다.")
        void matchByContent_withSort(String sortKey, Sort.Direction direction, List<Long> expected) {
            // given
            var content = "test";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10, Sort.by(direction, sortKey));

            // when
            var result = postRepository.matchByContent(content, emptyQueryParams, testPageable);

            // then
            assertEquals(3, result.getTotalElements());
            assertTrue(ListValidator.isSameList(result.stream().map(Post::getId).toList(), expected));
        }

    }

    @Nested
    @DisplayName("postRepository.matchByMix")
    class matchByMix {

        @DisplayName("내용으로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void matchByMix_successWithContent() {
            // given
            var content = "corn";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByMix(content, emptyQueryParams, testPageable);

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getContent().getPlainText().contains(content));
        }

        @DisplayName("제목으로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void matchByMix_successWithTitle() {
            // given
            var title = "corn";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10);

            // when
            var result = postRepository.matchByMix(title, emptyQueryParams, testPageable);

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getTitle().contains(title));
        }

        @DisplayName("내용 혹은 제목으로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void matchByMix_successWithTitleOrContent() {
            // given
            var keyword = "test";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByMix(keyword, emptyQueryParams, testPageable);

            // then
            assertEquals(3, result.getTotalElements());
            assertTrue(result.getContent().get(0).getContent().getPlainText().contains(keyword) ||
                    result.getContent().get(0).getTitle().contains(keyword));
            assertTrue(result.getContent().get(1).getContent().getPlainText().contains(keyword) ||
                    result.getContent().get(1).getTitle().contains(keyword));
            assertTrue(result.getContent().get(2).getContent().getPlainText().contains(keyword) ||
                    result.getContent().get(2).getTitle().contains(keyword));
        }

        private static Stream<Arguments> provideQueryParam() throws Throwable {

            var querySet = new HashSet<KeyValueItem<PostQueryKeys>>();

            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.POST_TYPE, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.SERIES, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.TAG, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.IS_ONLY_OPEN_POST, "true"));

            var allCombinations = CombinationGenerator.generate(querySet);

            return allCombinations.stream().map(Arguments::of);
        }

        @DisplayName("제목과 주어진 쿼리 파라미터로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/tag/post-tag.yaml",
                "datasets/repository/post/title-match-queryparam-post.yaml",
                "datasets/repository/postTag/base-postTag.yaml"
        })
        @MethodSource("provideQueryParam")
        @ParameterizedTest(name = "{index} : {0}")
        void matchByMix_successWithTitleAndQueryParam(List<KeyValueItem<PostQueryKeys>> queryParam) {
            // given
            var title = "corn";

            var queryParams = new LinkedMultiValueMap<PostQueryKeys, String>();
            queryParam.forEach(item -> queryParams.add(item.key(), item.value()));

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByMix(title, queryParams, testPageable);

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getTitle().contains(title));
            assertTrue(validateWithQueryKey(result.getContent().get(0), queryParams));
        }

        @DisplayName("본문과 주어진 쿼리 파라미터로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/content-match-content.yaml",
                "datasets/repository/tag/post-tag.yaml",
                "datasets/repository/post/title-match-queryparam-post.yaml",
                "datasets/repository/postTag/base-postTag.yaml"
        })
        @MethodSource("provideQueryParam")
        @ParameterizedTest(name = "{index} : {0}")
        void matchByMix_successWithContentAndQueryParam(List<KeyValueItem<PostQueryKeys>> queryParam) {
            // given
            var content = "corn";

            var queryParams = new LinkedMultiValueMap<PostQueryKeys, String>();
            queryParam.forEach(item -> queryParams.add(item.key(), item.value()));

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByMix(content, queryParams, testPageable);

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(result.getContent().get(0).getTitle().contains(content));
            assertTrue(validateWithQueryKey(result.getContent().get(0), queryParams));
        }

        @DisplayName("본문 혹은 제목과 주어진 쿼리 파라미터로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/mix-match-content.yaml",
                "datasets/repository/tag/post-tag.yaml",
                "datasets/repository/post/mix-match-queryparam-post.yaml",
                "datasets/repository/postTag/mix-match-postTag.yaml"
        })
        @MethodSource("provideQueryParam")
        @ParameterizedTest(name = "{index} : {0}")
        void matchByMix_successWithContentOrTitleAndQueryParam(List<KeyValueItem<PostQueryKeys>> queryParam) {
            // given
            var keyword = "corn";

            var queryParams = new LinkedMultiValueMap<PostQueryKeys, String>();
            queryParam.forEach(item -> queryParams.add(item.key(), item.value()));

            var testPageable = PageRequest.of(0, 10);
            // when
            var result = postRepository.matchByMix(keyword, queryParams, testPageable);

            // then
            assertEquals(2, result.getTotalElements());

            assertTrue(result.getContent().get(0).getTitle().contains(keyword) ||
                    result.getContent().get(0).getContent().getPlainText().contains(keyword));
            assertTrue(validateWithQueryKey(result.getContent().get(0), queryParams));

            assertTrue(result.getContent().get(1).getTitle().contains(keyword) ||
                    result.getContent().get(1).getContent().getPlainText().contains(keyword));
            assertTrue(validateWithQueryKey(result.getContent().get(1), queryParams));
        }

        private static Stream<Arguments> provideSortKey() {
            return Stream.of(
                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("title", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("title", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("content", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("content", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("isOpen", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("isOpen", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("series", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("series", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("createdDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("createdDate", Sort.Direction.DESC, List.of(1L, 2L, 3L))
            );
        }

        @DisplayName("제목으로 매칭되는 Post 를 정렬하여 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/sort-post.yaml"
        })
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "{index} : 정렬 키가 {0} 이고, 정렬 방향이 {1} 일 때, {2} 순서로 조회된다.")
        void matchByMix_withSort(String sortKey, Sort.Direction direction, List<Long> expected) {
            // given
            var title = "corn";

            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10, Sort.by(direction, sortKey));
            // when
            var result = postRepository.matchByMix(title, emptyQueryParams, testPageable);

            // then
            assertEquals(3, result.getTotalElements());
            assertTrue(ListValidator.isSameList(result.stream().map(Post::getId).toList(), expected));
        }

    }

    @Nested
    @DisplayName("postRepository.findAllPost")
    class findAllPost {
        private static Stream<Arguments> provideSortKey() {
            return Stream.of(
                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("title", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("title", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("content", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("content", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("isOpen", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("isOpen", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("series", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("series", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("createdDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("createdDate", Sort.Direction.DESC, List.of(1L, 2L, 3L))
            );
        }

        @DisplayName("Post와 정렬키가 주어지면, 해당 Post  를 정렬하여 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/sort-post.yaml"
        })
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "{index} : 정렬 키가 {0} 이고, 정렬 방향이 {1} 일 때, {2} 순서로 조회된다.")
        void findAllPost_withSort(String sortKey, Sort.Direction direction, List<Long> expected) {
            // given
            var testPageable = PageRequest.of(0, 10, Sort.by(direction, sortKey));

            // when
            var result = postRepository.findAllPost(testPageable);

            // then
            assertEquals(3, result.getTotalElements());
            assertTrue(ListValidator.isSameList(result.stream().map(Post::getId).toList(), expected));
        }

    }

    @Nested
    @DisplayName("postRepository.findAllPostWithQueryParam")
    class findAllPostWithQueryParam {
        private static Stream<Arguments> provideQueryParam() throws Throwable {

            var querySet = new HashSet<KeyValueItem<PostQueryKeys>>();

            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.POST_TYPE, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.SERIES, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.TAG, "1"));
            querySet.add(new KeyValueItem<PostQueryKeys>(PostQueryKeys.IS_ONLY_OPEN_POST, "true"));

            var allCombinations = CombinationGenerator.generate(querySet);

            return allCombinations.stream().map(Arguments::of);
        }

        @DisplayName("주어진 쿼리 파라미터로 매칭되는 Post 를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/tag/post-tag.yaml",
                "datasets/repository/post/title-match-queryparam-post.yaml",
                "datasets/repository/postTag/base-postTag.yaml"
        })
        @MethodSource("provideQueryParam")
        @ParameterizedTest(name = "{index} : {0}")
        void findAllPost_successWithQueryParam(List<KeyValueItem<PostQueryKeys>> queryParam) {
            // given
            var queryParams = new LinkedMultiValueMap<PostQueryKeys, String>();
            queryParam.forEach(item -> queryParams.add(item.key(), item.value()));

            var testPageable = PageRequest.of(0, 10);

            // when
            var result = postRepository.findAllPostWithQueryParam(queryParams, testPageable);

            // then
            assertEquals(1, result.getTotalElements());
            assertTrue(validateWithQueryKey(result.getContent().get(0), queryParams));
        }

        private static Stream<Arguments> provideSortKey() {
            return Stream.of(
                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("title", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("title", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("content", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("content", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("isOpen", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("isOpen", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("series", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("series", Sort.Direction.DESC, List.of(2L, 1L, 3L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("createdDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("createdDate", Sort.Direction.DESC, List.of(1L, 2L, 3L))
            );
        }

        @DisplayName("Post와 정렬키가 주어지면, 해당 Post  를 정렬하여 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/sort-post.yaml"
        })
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "{index} : 정렬 키가 {0} 이고, 정렬 방향이 {1} 일 때, {2} 순서로 조회된다.")
        void findAllPost_withSort(String sortKey, Sort.Direction direction, List<Long> expected) {
            // given
            var emptyQueryParams = new LinkedMultiValueMap<PostQueryKeys, String>();

            var testPageable = PageRequest.of(0, 10, Sort.by(direction, sortKey));
            // when
            var result = postRepository.findAllPostWithQueryParam(emptyQueryParams, testPageable);

            // then
            assertEquals(3, result.getTotalElements());
            assertTrue(ListValidator.isSameList(result.stream().map(Post::getId).toList(), expected));
        }

    }

    @Nested
    @DisplayName("postRepository.save")
    class save {

        @DisplayName("올바른 Post가 주어지면, Post 를 저장한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/empty-post.yaml"
        })
        @ExpectedDataSet(value = {
                "datasets/repository/post/saved-post.yaml"
        })
        @Test
        void save_success() {
            // given
            var post = Post.builder()
                    .title("test")
                    .thumbnail("test")
                    .content(Content.builder().id(1L).build())
                    .postType(PostType.builder().id(1L).build())
                    .series(Series.builder().id(1L).build())
                    .isEnabled(true)
                    .build();

            // when
            var savedPost = postRepository.save(post);

        }

    }

    @Nested
    @DisplayName("postRepository.delete")
    class delete {

        @DisplayName("올바른 Post Id가 주어지면, Post 를 삭제한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/tag/post-tag.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/postTag/base-postTag.yaml",
                "datasets/repository/user/base-user.yaml",
                "datasets/repository/comment/base-comment.yaml"
        })
        @ExpectedDataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/tag/post-tag.yaml",
                "datasets/repository/user/base-user.yaml",
                "datasets/repository/post/deleted-post.yaml",
                "datasets/repository/postTag/deleted-postTag.yaml",
                "datasets/repository/comment/empty-comment.yaml"
        })
        @Test
        void delete_success() {
            // given
            var validId = 1L;

            // when
            postRepository.deleteById(validId);
            postRepository.flush();

        }

    }

    @DisplayName("postRepository.findAllBySeries")
    @Nested
    class findAllBySeries {

        @DisplayName("시리즈 ID로 포스트를 조회한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void findAllBySeries_success() {
            // given
            var seriesId = 1L;

            // when
            var posts = postRepository.findAllBySeries(Series.builder().id(seriesId).build());

            // then
            assertEquals(1, posts.size());
        }

        @DisplayName("시리즈 ID로 포스트를 조회한다. - 시리즈가 없는 경우")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml"
        })
        @Test
        void findAllBySeries_failWithInvalidSeriesId() {
            // given
            var invalidSeriesId = 100L;

            // when
            var posts = postRepository.findAllBySeries(Series.builder().id(invalidSeriesId).build());

            // then
            assertEquals(0, posts.size());
        }

    }

    private boolean validateWithQueryKey(Post post, MultiValueMap<PostQueryKeys, String> queryParams) {
        for (var key : queryParams.keySet()) {

            var value = queryParams.getFirst(key);

            if (value == null) {
                return false;
            }

            switch (key) {


                case POST_TYPE -> {
                    if (!post.getPostType().getId().toString().equals(value)) {
                        return false;
                    }
                }

                case SERIES -> {
                    if (!post.getSeries().getId().toString().equals(value)) {
                        return false;
                    }
                }

                case TAG -> {
                    var postTags = post.getPostTags();
                    var result = postTags.stream().anyMatch(postTag -> postTag.getTag().getId().toString().equals(value));
                    if (!result) {
                        return false;
                    }
                }

                case IS_ONLY_OPEN_POST -> {
                    if (post.isEnabled() != Boolean.parseBoolean(value)) {
                        return false;
                    }
                }
                default -> {
                    return false;
                }
            }
        }
        return true;
    }
}
