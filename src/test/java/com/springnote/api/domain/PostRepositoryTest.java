package com.springnote.api.domain;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.post.PostRepository;
import com.springnote.api.domain.postTag.PostTagRepository;
import com.springnote.api.domain.postType.PostTypeRepository;
import com.springnote.api.domain.series.SeriesRepository;
import com.springnote.api.domain.tag.TagRepository;
import com.springnote.api.utils.testBase.BaseJpaTest;
import com.springnote.api.utils.testDataBuilder.TestPostBuilder;
import com.springnote.api.utils.testDataBuilder.TestPostTypeBuilder;
import com.springnote.api.utils.testDataBuilder.TestSeriesBuilder;
import com.springnote.api.utils.testDataBuilder.TestTagBuilder;
import com.springnote.api.utils.validator.ListValidator;

class PostRepositoryTest extends BaseJpaTest {

        @Autowired
        private PostRepository postRepository;

        @Autowired
        private SeriesRepository seriesRepository;

        @Autowired
        private PostTypeRepository postTypeRepository;

        @Autowired
        private PostTagRepository postTagRepository;

        @Autowired
        private TagRepository tagRepository;

        @Test
        void matchByTitle_ReturnTargetPosts_MatchedByTitle() {
                // given
                var testPostType = TestPostTypeBuilder.builder().build().toEntity();
                postTypeRepository.saveAndFlush(testPostType);

                var targetPost = TestPostBuilder.builder()
                                .title("이 게시글은 테스트에서 찾고자 하는 게시글입니다. 키워드 - 전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var nonTargetPost = TestPostBuilder.builder()
                                .title("이 게시글은 목표 키워드를 포함하지 않는 게시글입니다. 키워드 - 전역하기싫다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                postRepository.saveAllAndFlush(List.of(targetPost, nonTargetPost));

                var testPageRequest = PageRequest.of(0, 10);

                var targetKeyword = "전역하고싶다.";

                MultiValueMap<String, String> testSearchOptions = new LinkedMultiValueMap<>();

                // when
                Page<Post> result = postRepository.matchByTitle(targetKeyword, testSearchOptions, testPageRequest);

                // then
                assertEquals(1, result.getNumberOfElements());
                assertEquals(targetPost.getTitle(), result.getContent().get(0).getTitle());
        }

        private static Stream<Arguments> provideSearchOptionsForMatchedByTitleWithSearchOptions() throws Throwable {

                // 검색 옵션 ; 타켓 게시글 아이디
                return Stream.of(
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("series", List.of("1"))), List.of(1L)),
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("postType", List.of("1"))), List.of(2L)),
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("tag", List.of("1"))), List.of(3L)),
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("isOnlyOpenPost", List.of("false"))),
                                                List.of(4L)),

                                Arguments.of(new LinkedMultiValueMap<>(),
                                                List.of(1L, 2L, 3L, 4L)));
        }

        @ParameterizedTest(name = "{index} : {0} 검색 옵션이 주어졌을 때, {1} 게시글이 반환되어야 한다.")
        @MethodSource("provideSearchOptionsForMatchedByTitleWithSearchOptions")
        @Test
        void matchByTitle_ReturnTargetPosts_MatchedByTitleWithSearchOptions(MultiValueMap<String, String> searchOptions,
                        List<String> expectedPostIds) {
                // given
                var targetSeries = TestSeriesBuilder.builder()
                                .name("테스트 목표")
                                .build()
                                .toEntity();

                var nonTargetSeries = TestSeriesBuilder.builder()
                                .build()
                                .toEntity();

                seriesRepository.saveAllAndFlush(List.of(targetSeries, nonTargetSeries));

                var targetPostType = TestPostTypeBuilder.builder()
                                .id(1L)
                                .name("테스트 목표")
                                .build()
                                .toEntity();

                var nonTargetPostType = TestPostTypeBuilder.builder()
                                .id(2L)
                                .build()
                                .toEntity();

                postTypeRepository.saveAllAndFlush(List.of(targetPostType, nonTargetPostType));

                var hasTargetSeriesPost = TestPostBuilder.builder()
                                .id(1L)
                                .title("검색 옵션 테스트 1 - 전역하고싶다.")
                                .series(targetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetPostTypePost = TestPostBuilder.builder()
                                .id(2L)
                                .title("검색 옵션 테스트 2 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(targetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetPostTagPost = TestPostBuilder.builder()
                                .id(3L)
                                .title("검색 옵션 테스트 3 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasClosedPost = TestPostBuilder.builder()
                                .id(4L)
                                .title("검색 옵션 테스트 4 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(false)
                                .build()
                                .toEntity();

                postRepository.saveAllAndFlush(
                                List.of(hasTargetSeriesPost, hasTargetPostTypePost, hasTargetPostTagPost,
                                                hasClosedPost));

                var targetTag = TestTagBuilder.builder()
                                .id(1L)
                                .name("테스트 목표")
                                .build()
                                .toEntity();

                var nonTargetTag = TestTagBuilder.builder()
                                .id(2L)
                                .build()
                                .toEntity();

                tagRepository.saveAllAndFlush(List.of(targetTag, nonTargetTag));

                var hasTargetSeriesPostTag = nonTargetTag.toPostTag(hasTargetSeriesPost);
                var hasTargetPostTypePostTag = nonTargetTag.toPostTag(hasTargetPostTypePost);
                var hasClosedPostTag = nonTargetTag.toPostTag(hasClosedPost);

                var hasTargetPostTagPostTag = targetTag.toPostTag(hasTargetPostTagPost);

                postTagRepository.saveAllAndFlush(List.of(hasTargetSeriesPostTag, hasTargetPostTypePostTag,
                                hasTargetPostTagPostTag, hasClosedPostTag));

                var testPageRequest = PageRequest.of(0, 10);

                var targetKeyword = "전역하고싶다.";
                // when
                Page<Post> result = postRepository.matchByTitle(targetKeyword, searchOptions, testPageRequest);

                // then
                assertEquals(expectedPostIds.size(), result.getNumberOfElements());
                ListValidator.isSameList(expectedPostIds, result.getContent().stream().map(Post::getId).toList());

        }

        private static Stream<Arguments> provideSortOptionsForMatchByTitleOrderOptionTest() throws Throwable {

                // 정렬 옵션 ; 정렬 방향
                return Stream.of(
                                Arguments.of("id", Direction.ASC),
                                Arguments.of("id", Direction.DESC),
                                Arguments.of("views", Direction.ASC),
                                Arguments.of("views", Direction.DESC),
                                Arguments.of("likes", Direction.ASC),
                                Arguments.of("likes", Direction.DESC),
                                Arguments.of("title", Direction.ASC),
                                Arguments.of("title", Direction.DESC),
                                Arguments.of("createdDate", Direction.ASC),
                                Arguments.of("createdDate", Direction.DESC),
                                Arguments.of("lastModifiedDate", Direction.ASC),
                                Arguments.of("lastModifiedDate", Direction.DESC));
        }

        @ParameterizedTest(name = "{index} : {0} 을 기준으로, {1} 방향으로 정렬된 결과를 반환.")
        @MethodSource("provideSortOptionsForMatchByTitleOrderOptionTest")
        @Test
        void matchByTitle_ReturnSortedTargetPosts_MatchedByTitleAndOrderOptions(String sortOption,
                        Direction direction) {
                var testPostType = TestPostTypeBuilder.builder()
                                .build()
                                .toEntity();

                postTypeRepository.saveAndFlush(testPostType);

                var lowestTargetPost = TestPostBuilder.builder()
                                .id(1L)
                                .viewCnt(1L)
                                .likeCnt(1L)
                                .createdDate(LocalDateTime.of(2002, 8, 28, 0, 0, 0, 0))
                                .lastModifiedDate(LocalDateTime.of(2002, 8, 28, 0, 0, 0, 0))
                                .title("1. 나의 첫번째 꿈은 전역이다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var midleTargetPost = TestPostBuilder.builder()
                                .id(2L)
                                .viewCnt(2L)
                                .likeCnt(2L)
                                .createdDate(LocalDateTime.of(2002, 8, 29, 0, 0, 0, 0))
                                .lastModifiedDate(LocalDateTime.of(2002, 8, 29, 0, 0, 0, 0))
                                .title("2. 나의 두번째 꿈도 전역이다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var highestTargetPost = TestPostBuilder.builder()
                                .id(3L)
                                .viewCnt(3L)
                                .likeCnt(3L)
                                .createdDate(LocalDateTime.of(2002, 8, 30, 0, 0, 0, 0))
                                .lastModifiedDate(LocalDateTime.of(2002, 8, 30, 0, 0, 0, 0))
                                .title("3. 나의 마지막 꿈도 전역이다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                postRepository.saveAllAndFlush(List.of(lowestTargetPost, midleTargetPost, highestTargetPost));

                var testPageRequest = PageRequest.of(0, 10);
                testPageRequest = testPageRequest.withSort(Sort.by(direction, sortOption));

                var targetKeyword = "전역이다";

                MultiValueMap<String, String> testSearchOptions = new LinkedMultiValueMap<>();

                // when
                Page<Post> result = postRepository.matchByTitle(targetKeyword, testSearchOptions, testPageRequest);

                // then
                assertEquals(3, result.getNumberOfElements(), "Page 의 요소개수는 3개이어야 합니다.");

                assertEquals(lowestTargetPost,
                                result.getContent().get((direction == Direction.ASC) ? 0 : 2),
                                "lowestTargetPost가 가장" + ((direction == Direction.ASC) ? "먼저" : "나중에") + " 나와야 합니다.");
                assertEquals(midleTargetPost, result.getContent().get(1),
                                "midleTargetPost가 가장 중간에 나와야 합니다.");
                assertEquals(highestTargetPost, result.getContent().get((direction == Direction.ASC) ? 2 : 0),
                                "highestTargetPost가 가장" + ((direction == Direction.ASC) ? "나중에" : "먼저") + " 나와야 합니다.");
        }

        @Test
        void matchByContent_ReturnTargetPosts_MatchedByContent() {
                // given
                var testPostType = TestPostTypeBuilder.builder().build().toEntity();
                postTypeRepository.saveAndFlush(testPostType);

                var targetPost = TestPostBuilder.builder()
                                .content("이 포스트는 테스트에서 찾고자 하는 포스트 입니다. 아 전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity(1);

                var nonTargetPost = TestPostBuilder.builder()
                                .content("이 포스트는 테스트에서 찾고 싶지 않는 포스트입니다. 아 전역하기싫다.")
                                .postType(testPostType)
                                .build()
                                .toEntity(2);

                postRepository.saveAllAndFlush(List.of(targetPost, nonTargetPost));

                var testPageRequest = PageRequest.of(0, 10);

                var targetKeyword = "전역하고싶다.";

                MultiValueMap<String, String> testSearchOptions = new LinkedMultiValueMap<>();

                // when
                Page<Post> result = postRepository.matchByContent(targetKeyword, testSearchOptions, testPageRequest);

                // then
                assertEquals(1, result.getNumberOfElements());
                assertEquals(targetPost.getContent(), result.getContent().get(0).getContent());
        }

        private static Stream<Arguments> provideSearchOptionsForMatchedByContentWithSearchOptions() throws Throwable {

                // 검색 옵션 ; 타켓 게시글 아이디
                return Stream.of(
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("series", List.of("1"))), List.of(1L)),
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("postType", List.of("1"))), List.of(2L)),
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("tag", List.of("1"))), List.of(3L)),
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("isOnlyOpenPost", List.of("false"))),
                                                List.of(4L)),

                                Arguments.of(new LinkedMultiValueMap<>(),
                                                List.of(1L, 2L, 3L, 4L)));
        }

        @ParameterizedTest(name = "{index} : {0} 검색 옵션이 주어졌을 때, {1} 게시글이 반환되어야 한다.")
        @MethodSource("provideSearchOptionsForMatchedByContentWithSearchOptions")
        @Test
        void matchByContent_ReturnTargetPosts_MatchedByContentWithSearchOptions(
                        MultiValueMap<String, String> searchOptions,
                        List<String> expectedPostIds) {
                // given
                var targetSeries = TestSeriesBuilder.builder()
                                .name("테스트 목표")
                                .build()
                                .toEntity();

                var nonTargetSeries = TestSeriesBuilder.builder()
                                .build()
                                .toEntity();

                seriesRepository.saveAllAndFlush(List.of(targetSeries, nonTargetSeries));

                var targetPostType = TestPostTypeBuilder.builder()
                                .id(1L)
                                .name("테스트 목표")
                                .build()
                                .toEntity();

                var nonTargetPostType = TestPostTypeBuilder.builder()
                                .id(2L)
                                .build()
                                .toEntity();

                postTypeRepository.saveAllAndFlush(List.of(targetPostType, nonTargetPostType));

                var hasTargetSeriesPost = TestPostBuilder.builder()
                                .id(1L)
                                .content("검색 옵션 테스트 1 - 전역하고싶다.")
                                .series(targetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity(1);

                var hasTargetPostTypePost = TestPostBuilder.builder()
                                .id(2L)
                                .content("검색 옵션 테스트 2 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(targetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity(2);

                var hasTargetPostTagPost = TestPostBuilder.builder()
                                .id(3L)
                                .content("검색 옵션 테스트 3 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity(3);

                var hasClosedPost = TestPostBuilder.builder()
                                .id(4L)
                                .content("검색 옵션 테스트 4 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(false)
                                .build()
                                .toEntity(4);

                postRepository.saveAllAndFlush(
                                List.of(hasTargetSeriesPost, hasTargetPostTypePost, hasTargetPostTagPost,
                                                hasClosedPost));

                var targetTag = TestTagBuilder.builder()
                                .id(1L)
                                .name("테스트 목표")
                                .build()
                                .toEntity();

                var nonTargetTag = TestTagBuilder.builder()
                                .id(2L)
                                .build()
                                .toEntity();

                tagRepository.saveAllAndFlush(List.of(targetTag, nonTargetTag));

                var hasTargetSeriesPostTag = nonTargetTag.toPostTag(hasTargetSeriesPost);
                var hasTargetPostTypePostTag = nonTargetTag.toPostTag(hasTargetPostTypePost);
                var hasClosedPostTag = nonTargetTag.toPostTag(hasClosedPost);

                var hasTargetPostTagPostTag = targetTag.toPostTag(hasTargetPostTagPost);

                postTagRepository.saveAllAndFlush(List.of(hasTargetSeriesPostTag, hasTargetPostTypePostTag,
                                hasTargetPostTagPostTag, hasClosedPostTag));

                var testPageRequest = PageRequest.of(0, 10);

                var targetKeyword = "전역하고싶다.";
                // when
                Page<Post> result = postRepository.matchByContent(targetKeyword, searchOptions, testPageRequest);

                // then
                assertEquals(expectedPostIds.size(), result.getNumberOfElements());
                ListValidator.isSameList(expectedPostIds, result.getContent().stream().map(Post::getId).toList());

        }

        private static Stream<Arguments> provideSortOptionsForMatchByContentOrderOptionTest() throws Throwable {

                // 정렬 옵션 ; 정렬 방향
                return Stream.of(
                                Arguments.of("id", Direction.ASC),
                                Arguments.of("id", Direction.DESC),
                                Arguments.of("views", Direction.ASC),
                                Arguments.of("views", Direction.DESC),
                                Arguments.of("likes", Direction.ASC),
                                Arguments.of("likes", Direction.DESC),
                                Arguments.of("title", Direction.ASC),
                                Arguments.of("title", Direction.DESC),
                                Arguments.of("createdDate", Direction.ASC),
                                Arguments.of("createdDate", Direction.DESC),
                                Arguments.of("lastModifiedDate", Direction.ASC),
                                Arguments.of("lastModifiedDate", Direction.DESC));
        }

        @ParameterizedTest(name = "{index} : {0} 을 기준으로, {1} 방향으로 정렬된 결과를 반환.")
        @MethodSource("provideSortOptionsForMatchByContentOrderOptionTest")
        @Test
        void matchByContent_ReturnSortedTargetPosts_MatchedByContentAndOrderOptions(String sortOption,
                        Direction direction) {
                var testPostType = TestPostTypeBuilder.builder()
                                .build()
                                .toEntity();

                postTypeRepository.saveAndFlush(testPostType);

                var lowestTargetPost = TestPostBuilder.builder()
                                .id(1L)
                                .viewCnt(1L)
                                .likeCnt(1L)
                                .createdDate(LocalDateTime.of(2002, 8, 28, 0, 0, 0, 0))
                                .lastModifiedDate(LocalDateTime.of(2002, 8, 28, 0, 0, 0, 0))
                                .title("1. 나의 첫번째 꿈은 전역이다.")
                                .content("전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var midleTargetPost = TestPostBuilder.builder()
                                .id(2L)
                                .viewCnt(2L)
                                .likeCnt(2L)
                                .createdDate(LocalDateTime.of(2002, 8, 29, 0, 0, 0, 0))
                                .lastModifiedDate(LocalDateTime.of(2002, 8, 29, 0, 0, 0, 0))
                                .title("2. 나의 두번째 꿈도 전역이다.")
                                .content("전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var highestTargetPost = TestPostBuilder.builder()
                                .id(3L)
                                .viewCnt(3L)
                                .likeCnt(3L)
                                .createdDate(LocalDateTime.of(2002, 8, 30, 0, 0, 0, 0))
                                .lastModifiedDate(LocalDateTime.of(2002, 8, 30, 0, 0, 0, 0))
                                .title("3. 나의 마지막 꿈도 전역이다.")
                                .content("전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                postRepository.saveAllAndFlush(List.of(lowestTargetPost, midleTargetPost, highestTargetPost));

                var testPageRequest = PageRequest.of(0, 10);
                testPageRequest = testPageRequest.withSort(Sort.by(direction, sortOption));

                var targetKeyword = "전역하고싶다.";

                MultiValueMap<String, String> testSearchOptions = new LinkedMultiValueMap<>();

                // when
                Page<Post> result = postRepository.matchByContent(targetKeyword, testSearchOptions, testPageRequest);

                // then
                assertEquals(3, result.getNumberOfElements(), "Page 의 요소개수는 3개이어야 합니다.");

                assertEquals(lowestTargetPost,
                                result.getContent().get((direction == Direction.ASC) ? 0 : 2),
                                "lowestTargetPost가 가장" + ((direction == Direction.ASC) ? "먼저" : "나중에") + " 나와야 합니다.");
                assertEquals(midleTargetPost, result.getContent().get(1),
                                "midleTargetPost가 가장 중간에 나와야 합니다.");
                assertEquals(highestTargetPost, result.getContent().get((direction == Direction.ASC) ? 2 : 0),
                                "highestTargetPost가 가장" + ((direction == Direction.ASC) ? "나중에" : "먼저") + " 나와야 합니다.");
        }

        @Test
        void matchByTitleOrContent_ReturnTargetPosts_MatchedByKeyword() {
                // given
                var testPostType = TestPostTypeBuilder.builder().build().toEntity();
                postTypeRepository.saveAndFlush(testPostType);

                var targetPostHasKeywordInTitle = TestPostBuilder.builder()
                                .id(1L)
                                .title("제목에 키워드를 가지고 있는 포스트 아 전역하고싶다.")
                                .content("본문에는 없지요")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var targetPostHasKeywordInContent = TestPostBuilder.builder()
                                .id(2L)
                                .title("제목에는 키워드가 없지만,")
                                .content("본문에는 가지고 있지요. 아 전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var targetPostHasKeywordBoth = TestPostBuilder.builder()
                                .id(3L)
                                .title("제목에도 키워드가 있고, 아 전역하고싶다.")
                                .content("본문에도 가지고 있지요. 아 전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var nonTargetPost = TestPostBuilder.builder()
                                .id(4L)
                                .title("얘는 키워드가 없습니다.")
                                .content("본문에도 없지요.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                postRepository.saveAllAndFlush(List.of(targetPostHasKeywordInTitle, targetPostHasKeywordInContent,
                                targetPostHasKeywordBoth, nonTargetPost));

                var testPageRequest = PageRequest.of(0, 10);

                var targetKeyword = "아 전역하고싶다.";

                MultiValueMap<String, String> testSearchOptions = new LinkedMultiValueMap<>();

                // when
                Page<Post> result = postRepository.matchByTitleOrContent(targetKeyword, testSearchOptions, testPageRequest);

                // then
                assertEquals(3, result.getNumberOfElements());
                ListValidator.isSameList(List.of(targetPostHasKeywordInTitle, targetPostHasKeywordInContent,
                                targetPostHasKeywordBoth), result.getContent());
        }

        private static Stream<Arguments> provideSearchOptionsForMatchedByTitleOrContentWithSearchOptions()
                        throws Throwable {

                // 검색 옵션 ; 타켓 게시글 아이디
                return Stream.of(
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("series", List.of("1"))), List.of(1L,2L,3L)),
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("postType", List.of("1"))), List.of(4L,5L,6L)),
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("tag", List.of("1"))), List.of(7L,8L,9L)),
                                Arguments.of(new LinkedMultiValueMap<>(Map.of("isOnlyOpenPost", List.of("false"))),
                                                List.of(10L,11L,12L)),

                                Arguments.of(new LinkedMultiValueMap<>(),
                                                List.of(1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L)));
        }

        @ParameterizedTest(name = "{index} : {0} 검색 옵션이 주어졌을 때, {1} 게시글이 반환되어야 한다.")
        @MethodSource("provideSearchOptionsForMatchedByTitleOrContentWithSearchOptions")
        @Test
        void matchByTitleOrContent_ReturnTargetPosts_MatchedByTitleOrContentWithSearchOptions(
                        MultiValueMap<String, String> searchOptions,
                        List<String> expectedPostIds) {
                // given
                var targetSeries = TestSeriesBuilder.builder()
                                .name("테스트 목표")
                                .build()
                                .toEntity();

                var nonTargetSeries = TestSeriesBuilder.builder()
                                .build()
                                .toEntity();

                seriesRepository.saveAllAndFlush(List.of(targetSeries, nonTargetSeries));

                var targetPostType = TestPostTypeBuilder.builder()
                                .id(1L)
                                .name("테스트 목표")
                                .build()
                                .toEntity();

                var nonTargetPostType = TestPostTypeBuilder.builder()
                                .id(2L)
                                .build()
                                .toEntity();

                postTypeRepository.saveAllAndFlush(List.of(targetPostType, nonTargetPostType));

                var hasTargetSeriesPostAndHasKeywordInTitle = TestPostBuilder.builder()
                                .id(1L)
                                .title("검색 옵션 테스트 1 - 전역하고싶다.")
                                .content("검색 옵션 테스트 1")
                                .series(targetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetSeriesPostAndHasKeywordInContent = TestPostBuilder.builder()
                                .id(2L)
                                .title("검색 옵션 테스트 2 ")
                                .content("검색 옵션 테스트 2 - 전역하고싶다.")
                                .series(targetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetSeriesPostAndHasKeywordBoth = TestPostBuilder.builder()
                                .id(3L)
                                .title("검색 옵션 테스트 3 - 전역하고싶다.")
                                .content("검색 옵션 테스트 3 - 전역하고싶다.")
                                .series(targetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetPostTypePostAndHasKeywordInTitle = TestPostBuilder.builder()
                                .id(4L)
                                .title("검색 옵션 테스트 4 - 전역하고싶다.")
                                .content("검색 옵션 테스트 4")
                                .series(nonTargetSeries)
                                .postType(targetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetPostTypePostAndHasKeywordInContent = TestPostBuilder.builder()
                                .id(5L)
                                .title("검색 옵션 테스트 5")
                                .content("검색 옵션 테스트 5 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(targetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetPostTypePostAndHasKeywordBoth = TestPostBuilder.builder()
                                .id(6L)
                                .title("검색 옵션 테스트 6 - 전역하고싶다.")
                                .content("검색 옵션 테스트 6 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(targetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetPostTagPostAndHasKeywordInTitle = TestPostBuilder.builder()
                                .id(7L)
                                .title("검색 옵션 테스트 7 - 전역하고싶다.")
                                .content("검색 옵션 테스트 7")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetPostTagPostAndHasKeywordInContent = TestPostBuilder.builder()
                                .id(8L)
                                .title("검색 옵션 테스트 8")
                                .content("검색 옵션 테스트 8 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasTargetPostTagPostAndHasKeywordBoth = TestPostBuilder.builder()
                                .id(9L)
                                .title("검색 옵션 테스트 9 - 전역하고싶다.")
                                .content("검색 옵션 테스트 9 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(true)
                                .build()
                                .toEntity();

                var hasClosedPostAndHasKeywordInTitle = TestPostBuilder.builder()
                                .id(10L)
                                .title("검색 옵션 테스트 10 - 전역하고싶다.")
                                .content("검색 옵션 테스트 10")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(false)
                                .build()
                                .toEntity();

                var hasClosedPostAndHasKeywordInContent = TestPostBuilder.builder()
                                .id(11L)
                                .title("검색 옵션 테스트 11")
                                .content("검색 옵션 테스트 11 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(false)
                                .build()
                                .toEntity();

                var hasClosedPostAndHasKeywordBoth = TestPostBuilder.builder()
                                .id(12L)
                                .title("검색 옵션 테스트 12 - 전역하고싶다.")
                                .content("검색 옵션 테스트 12 - 전역하고싶다.")
                                .series(nonTargetSeries)
                                .postType(nonTargetPostType)
                                .isOpen(false)
                                .build()
                                .toEntity();

                postRepository.saveAllAndFlush(List.of(
                                hasTargetSeriesPostAndHasKeywordInTitle, hasTargetSeriesPostAndHasKeywordInContent,
                                hasTargetSeriesPostAndHasKeywordBoth,
                                hasTargetPostTypePostAndHasKeywordInTitle, hasTargetPostTypePostAndHasKeywordInContent,
                                hasTargetPostTypePostAndHasKeywordBoth,
                                hasTargetPostTagPostAndHasKeywordInTitle, hasTargetPostTagPostAndHasKeywordInContent,
                                hasTargetPostTagPostAndHasKeywordBoth,
                                hasClosedPostAndHasKeywordInTitle, hasClosedPostAndHasKeywordInContent,
                                hasClosedPostAndHasKeywordBoth));

                var targetTag = TestTagBuilder.builder()
                                .id(1L)
                                .name("테스트 목표")
                                .build()
                                .toEntity();

                var nonTargetTag = TestTagBuilder.builder()
                                .id(2L)
                                .build()
                                .toEntity();

                tagRepository.saveAllAndFlush(List.of(targetTag, nonTargetTag));

                var hasTargetSeriesPostTag = nonTargetTag.toPostTag(hasTargetSeriesPostAndHasKeywordInTitle);
                var hasTargetSeriesPostTagAndHasKeywordInTitle = nonTargetTag
                                .toPostTag(hasTargetSeriesPostAndHasKeywordInTitle);
                var hasTargetSeriesPostTagAndHasKeywordInContent = nonTargetTag
                                .toPostTag(hasTargetSeriesPostAndHasKeywordInContent);
                var hasTargetSeriesPostTagAndHasKeywordBoth = nonTargetTag
                                .toPostTag(hasTargetSeriesPostAndHasKeywordBoth);

                var hasTargetPostTypePostTagAndHasKeywordInTitle = nonTargetTag
                                .toPostTag(hasTargetPostTypePostAndHasKeywordInTitle);
                var hasTargetPostTypePostTagAndHasKeywordInContent = nonTargetTag
                                .toPostTag(hasTargetPostTypePostAndHasKeywordInContent);
                var hasTargetPostTypePostTagAndHasKeywordBoth = nonTargetTag
                                .toPostTag(hasTargetPostTypePostAndHasKeywordBoth);

                var hasTargetPostTagPostTagAndHasKeywordInTitle = targetTag
                                .toPostTag(hasTargetPostTagPostAndHasKeywordInTitle);
                var hasTargetPostTagPostTagAndHasKeywordInContent = targetTag
                                .toPostTag(hasTargetPostTagPostAndHasKeywordInContent);
                var hasTargetPostTagPostTagAndHasKeywordBoth = targetTag
                                .toPostTag(hasTargetPostTagPostAndHasKeywordBoth);

                var hasClosedPostTagAndHasKeywordInTitle = nonTargetTag.toPostTag(hasClosedPostAndHasKeywordInTitle);
                var hasClosedPostTagAndHasKeywordInContent = nonTargetTag
                                .toPostTag(hasClosedPostAndHasKeywordInContent);
                var hasClosedPostTagAndHasKeywordBoth = nonTargetTag.toPostTag(hasClosedPostAndHasKeywordBoth);

                postTagRepository.saveAllAndFlush(List.of(
                                hasTargetSeriesPostTag, hasTargetSeriesPostTagAndHasKeywordInTitle,
                                hasTargetSeriesPostTagAndHasKeywordInContent,
                                hasTargetSeriesPostTagAndHasKeywordBoth,
                                hasTargetPostTypePostTagAndHasKeywordInTitle,
                                hasTargetPostTypePostTagAndHasKeywordInContent,
                                hasTargetPostTypePostTagAndHasKeywordBoth,
                                hasTargetPostTagPostTagAndHasKeywordInTitle,
                                hasTargetPostTagPostTagAndHasKeywordInContent,
                                hasTargetPostTagPostTagAndHasKeywordBoth,
                                hasClosedPostTagAndHasKeywordInTitle, hasClosedPostTagAndHasKeywordInContent,
                                hasClosedPostTagAndHasKeywordBoth));

                var testPageRequest = PageRequest.of(0, 20);

                var targetKeyword = "전역하고싶다.";

                // when
                Page<Post> result = postRepository.matchByTitleOrContent(targetKeyword, searchOptions, testPageRequest);

                // then
                assertEquals(expectedPostIds.size(), result.getNumberOfElements());
                ListValidator.isSameList(expectedPostIds, result.getContent().stream().map(Post::getId).toList());

        }

        private static Stream<Arguments> provideSortOptionsForMatchByTitleOrContentOrderOptionTest() throws Throwable {

                // 정렬 옵션 ; 정렬 방향
                return Stream.of(
                                Arguments.of("id", Direction.ASC),
                                Arguments.of("id", Direction.DESC),
                                Arguments.of("views", Direction.ASC),
                                Arguments.of("views", Direction.DESC),
                                Arguments.of("likes", Direction.ASC),
                                Arguments.of("likes", Direction.DESC),
                                Arguments.of("title", Direction.ASC),
                                Arguments.of("title", Direction.DESC),
                                Arguments.of("createdDate", Direction.ASC),
                                Arguments.of("createdDate", Direction.DESC),
                                Arguments.of("lastModifiedDate", Direction.ASC),
                                Arguments.of("lastModifiedDate", Direction.DESC));
        }

        @ParameterizedTest(name = "{index} : {0} 을 기준으로, {1} 방향으로 정렬된 결과를 반환.")
        @MethodSource("provideSortOptionsForMatchByTitleOrContentOrderOptionTest")
        @Test
        void matchByTitleOrContent_ReturnSortedTargetPosts_MatchedByTitleOrContentAndOrderOptions(String sortOption,
                        Direction direction) {
                var testPostType = TestPostTypeBuilder.builder()
                                .build()
                                .toEntity();

                postTypeRepository.saveAndFlush(testPostType);

                var lowestTargetPost = TestPostBuilder.builder()
                                .id(1L)
                                .viewCnt(1L)
                                .likeCnt(1L)
                                .createdDate(LocalDateTime.of(2002, 8, 28, 0, 0, 0, 0))
                                .lastModifiedDate(LocalDateTime.of(2002, 8, 28, 0, 0, 0, 0))
                                .title("1. 나의 첫번째 꿈은 전역이다.")
                                .content("전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var midleTargetPost = TestPostBuilder.builder()
                                .id(2L)
                                .viewCnt(2L)
                                .likeCnt(2L)
                                .createdDate(LocalDateTime.of(2002, 8, 29, 0, 0, 0, 0))
                                .lastModifiedDate(LocalDateTime.of(2002, 8, 29, 0, 0, 0, 0))
                                .title("2. 나의 두번째 꿈도 전역이다.")
                                .content("전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                var highestTargetPost = TestPostBuilder.builder()
                                .id(3L)
                                .viewCnt(3L)
                                .likeCnt(3L)
                                .createdDate(LocalDateTime.of(2002, 8, 30, 0, 0, 0, 0))
                                .lastModifiedDate(LocalDateTime.of(2002, 8, 30, 0, 0, 0, 0))
                                .title("3. 나의 마지막 꿈도 전역이다.")
                                .content("전역하고싶다.")
                                .postType(testPostType)
                                .build()
                                .toEntity();

                postRepository.saveAllAndFlush(List.of(lowestTargetPost, midleTargetPost, highestTargetPost));

                var testPageRequest = PageRequest.of(0, 10);
                testPageRequest = testPageRequest.withSort(Sort.by(direction, sortOption));

                var targetKeyword = "전역하고싶다.";

                MultiValueMap<String, String> testSearchOptions = new LinkedMultiValueMap<>();

                // when
                Page<Post> result = postRepository.matchByTitleOrContent(targetKeyword, testSearchOptions, testPageRequest);

                // then
                assertEquals(3, result.getNumberOfElements(), "Page 의 요소개수는 3개이어야 합니다.");

                assertEquals(lowestTargetPost,
                                result.getContent().get((direction == Direction.ASC) ? 0 : 2),
                                "lowestTargetPost가 가장" + ((direction == Direction.ASC) ? "먼저" : "나중에") + " 나와야 합니다.");
                assertEquals(midleTargetPost, result.getContent().get(1),
                                "midleTargetPost가 가장 중간에 나와야 합니다.");
                assertEquals(highestTargetPost, result.getContent().get((direction == Direction.ASC) ? 2 : 0),
                                "highestTargetPost가 가장" + ((direction == Direction.ASC) ? "나중에" : "먼저") + " 나와야 합니다.");
        }
}
