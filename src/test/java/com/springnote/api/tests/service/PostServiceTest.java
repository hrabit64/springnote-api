package com.springnote.api.tests.service;

import com.springnote.api.domain.content.Content;
import com.springnote.api.domain.content.ContentRepository;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.post.PostRepository;
import com.springnote.api.domain.postTag.PostTag;
import com.springnote.api.domain.postTag.PostTagRepository;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.postType.PostTypeRepository;
import com.springnote.api.domain.series.SeriesRepository;
import com.springnote.api.domain.tag.TagRepository;
import com.springnote.api.dto.content.controller.ContentResponseControllerDto;
import com.springnote.api.dto.post.common.PostDetailResponseCommonDto;
import com.springnote.api.dto.post.common.PostSimpleResponseCommonDto;
import com.springnote.api.dto.post.service.PostCreateRequestServiceDto;
import com.springnote.api.dto.post.service.PostUpdateRequestServiceDto;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.dto.series.common.SeriesSimpleResponseDto;
import com.springnote.api.dto.tag.common.TagResponseDto;
import com.springnote.api.service.PostService;
import com.springnote.api.testUtils.dataFactory.post.PostTestDataFactory;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.markdown.MarkdownHelper;
import com.springnote.api.utils.tag.TagComparator;
import com.springnote.api.utils.tag.TagComparisonResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.*;
import static com.springnote.api.testUtils.dataFactory.content.ContentTestDataFactory.copyContent;
import static com.springnote.api.testUtils.dataFactory.content.ContentTestDataFactory.createContent;
import static com.springnote.api.testUtils.dataFactory.post.PostTestDataFactory.createFullyPost;
import static com.springnote.api.testUtils.dataFactory.post.PostTestDataFactory.createFullyPostWithTitle;
import static com.springnote.api.testUtils.dataFactory.postTag.PostTagTestDataFactory.copyPostTag;
import static com.springnote.api.testUtils.dataFactory.postType.PostTypeTestDataFactory.createSeriesPostType;
import static com.springnote.api.testUtils.dataFactory.series.SeriesTestDataFactory.createSeries;
import static com.springnote.api.testUtils.dataFactory.tag.TagTestDataFactory.createTag;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Service Test - PostService")
public class PostServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private PostService postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private ContentRepository contentRepository;

    @Mock
    private PostTagRepository postTagRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private MarkdownHelper markdownHelper;

    @Mock
    private PostTypeRepository postTypeRepository;

    @Mock
    private TagComparator<PostTag> tagComparator;

    @DisplayName("PostService.getById")
    @Nested
    class getById {

        @DisplayName("올바른 ID로 조회할 경우, 해당 ID의 Post를 반환한다.")
        @Test
        void getById_successWithValidId() {
            // given
            var validId = 1L;

            var targetPost = createFullyPost(validId);

            doReturn(Optional.of(targetPost)).when(postRepository).findById(eq(validId));

            // when
            var result = postService.getById(validId);

            // then
            var expected = PostDetailResponseCommonDto.builder()
                    .id(validId)
                    .title(targetPost.getTitle())
                    .content(ContentResponseControllerDto.builder()
                            .editorText(targetPost.getContent().getEditorText())
                            .plainText(targetPost.getContent().getPlainText())
                            .build())
                    .thumbnail(targetPost.getThumbnail())
                    .series(SeriesSimpleResponseDto.builder()
                            .id(targetPost.getSeries().getId())
                            .name(targetPost.getSeries().getName())
                            .build())
                    .tags(targetPost.getPostTags().stream().map(
                                    postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                            ).toList()
                    )
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // then
            assertEquals(expected, result);
            verify(postRepository).findById(eq(validId));
        }

        @DisplayName("올바르지 않은 ID로 조회할 경우, 에러가 발생한다.")
        @Test
        void getById_failWithInvalidId() {
            // given
            var invalidId = 999L;

            doReturn(Optional.empty()).when(postRepository).findById(eq(invalidId));

            // when
            var result = assertThrows(BusinessException.class, () -> postService.getById(invalidId));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(postRepository).findById(eq(invalidId));

        }
    }

    @DisplayName("PostService.create")
    @Nested
    class create {

        private static Stream<Arguments> provideValidPostCreateRequest() {
            var needSeries = PostCreateRequestServiceDto.builder()
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(1L)
                    .postTypeId(1L)
                    .tagIds(List.of(1L, 2L))
                    .isEnabled(true)
                    .build();

            var noSeries = PostCreateRequestServiceDto.builder()
                    .title("title")
                    .content("content")
                    .seriesId(null)
                    .thumbnail("thumbnail")
                    .postTypeId(1L)
                    .tagIds(List.of(1L, 2L))
                    .isEnabled(true)
                    .build();

            return Stream.of(
                    Arguments.of(needSeries, createSeriesPostType(true), "시리즈가 필요한 정상적인 게시글"),
                    Arguments.of(noSeries, createSeriesPostType(false), "시리즈가 불필요한 정상적인 게시글")
            );
        }

        @DisplayName("올바른 Post를 입력할 경우, Post를 생성한다.")
        @MethodSource("provideValidPostCreateRequest")
        @ParameterizedTest(name = "{index} : {2} 이 주어졌을 때 정상적으로 생성한다.")
        void create_successWithValidSeriesPost(PostCreateRequestServiceDto requestDto, PostType targetPostType, String description) {
            // given

            // 제목이 중복되지 않는지 먼저 검증한다.
            doReturn(false).when(postRepository).existsByTitle(eq(requestDto.getTitle()));

            // 현재 요청한 정보가 해당 포스트 타입의 정책과 일치하는지 검증한다.
            doReturn(Optional.of(targetPostType)).when(postTypeRepository).findById(eq(requestDto.getPostTypeId()));

            // 시리즈가 필요한 경우, 시리즈가 존재하는지 검증한다.
            if (requestDto.getSeriesId() != null)
                doReturn(Optional.of(createSeries(requestDto.getSeriesId()))).when(seriesRepository).findById(eq(requestDto.getSeriesId()));

            // 본문을 생성한다.
            var plainText = "content";

            doReturn(plainText).when(markdownHelper).toPlainText(eq(requestDto.getContent()));

            var targetContent = Content.builder().plainText(plainText).editorText(requestDto.getContent()).build();
            var savedContent = createContent(targetContent.getEditorText(), targetContent.getPlainText());
            doReturn(savedContent).when(contentRepository).save(eq(targetContent));

            // 포스트를 저장한다.
            var targetPost = Post.builder()
                    .title(requestDto.getTitle())
                    .content(targetContent)
                    .thumbnail(requestDto.getThumbnail())
                    .postType(targetPostType)
                    .isEnabled(requestDto.isEnabled())
                    .build();

            if (requestDto.getSeriesId() != null) targetPost.setSeries(createSeries(requestDto.getSeriesId()));

            var savedPost = createFullyPost(1L);

            if ((requestDto.getSeriesId() != null)) {
                savedPost.setSeries(createSeries(requestDto.getSeriesId()));
            } else {
                savedPost.setSeries(null);
            }
            savedPost.setPostType(targetPostType);
            savedPost.setContent(savedContent);
            savedPost.setTitle(requestDto.getTitle());
            savedPost.setThumbnail(requestDto.getThumbnail());
            savedPost.setEnabled(requestDto.isEnabled());
            savedPost.setPostTags(List.of());

            doReturn(savedPost).when(postRepository).save(eq(targetPost));

            // 태그를 검증하고, 태그를 등록한다.
            var tags = List.of(
                    createTag(1L, "1"),
                    createTag(2L, "2")
            );

            doReturn(tags).when(tagRepository).findTagsByIdIn(eq(requestDto.getTagIds()));

            var postTags = tags.stream().map(tag -> PostTag.builder().post(savedPost).tag(tag).build()).toList();
            var savedPostTags = postTags.stream().map(pt -> {
                var savedPostTag = copyPostTag(pt);
                savedPostTag.setId(pt.getId());
                return savedPostTag;
            }).toList();

            doReturn(savedPostTags).when(postTagRepository).saveAll(eq(postTags));

            // when
            var result = postService.create(requestDto);

            // then
            var expected = PostDetailResponseCommonDto.builder()
                    .id(1L)
                    .title(requestDto.getTitle())
                    .content(ContentResponseControllerDto.builder()
                            .editorText(requestDto.getContent())
                            .plainText(plainText)
                            .build())
                    .thumbnail(requestDto.getThumbnail())
                    .series((requestDto.getSeriesId() != null) ? SeriesSimpleResponseDto.builder().id(requestDto.getSeriesId()).name(savedPost.getSeries().getName()).build() : null)
                    .tags(tags.stream().map(tag -> TagResponseDto.builder().id(tag.getId()).name(tag.getName()).build()).toList())
                    .postType(PostTypeResponseDto.builder().id(targetPostType.getId()).name(targetPostType.getName()).build())
                    .createdAt(savedPost.getCreatedDate())
                    .lastUpdatedAt(savedPost.getLastModifiedDate())
                    .isEnabled(requestDto.isEnabled())
                    .build();

            assertEquals(expected, result);
            verify(postRepository).existsByTitle(eq(requestDto.getTitle()));
            verify(postTypeRepository).findById(eq(requestDto.getPostTypeId()));
            if (requestDto.getSeriesId() != null) verify(seriesRepository).findById(eq(requestDto.getSeriesId()));
            verify(markdownHelper).toPlainText(eq(requestDto.getContent()));
            verify(contentRepository).save(eq(targetContent));
            verify(postRepository).save(eq(targetPost));
            verify(tagRepository).findTagsByIdIn(eq(requestDto.getTagIds()));
            verify(postTagRepository).saveAll(eq(postTags));


        }

        private static Stream<Arguments> provideNotValidPostCreateRequest() {
            var needSeries = PostCreateRequestServiceDto.builder()
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(1L)
                    .postTypeId(1L)
                    .tagIds(List.of(1L, 2L))
                    .isEnabled(true)
                    .build();

            var noSeries = PostCreateRequestServiceDto.builder()
                    .title("title")
                    .content("content")
                    .seriesId(null)
                    .thumbnail("thumbnail")
                    .postTypeId(1L)
                    .tagIds(List.of(1L, 2L))
                    .isEnabled(true)
                    .build();

            return Stream.of(
                    Arguments.of(noSeries, createSeriesPostType(true), "시리즈가 필요한 게시글이지만, 시리즈가 존재하지 않는 경우"),
                    Arguments.of(needSeries, createSeriesPostType(false), "시리즈가 불필요한 게시글이지만, 시리즈가 존재하는 경우")
            );
        }

        @DisplayName("이미 존재하는 제목의 Post가 주어지면, 에러가 발생한다.")
        @Test
        void create_failWithExistTitle() {
            // given
            var request = PostCreateRequestServiceDto.builder()
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(1L)
                    .postTypeId(1L)
                    .tagIds(List.of(1L, 2L))
                    .isEnabled(true)
                    .build();
            // 제목이 중복되지 않는지 먼저 검증한다.
            doReturn(true).when(postRepository).existsByTitle(eq(request.getTitle()));


            // when
            var result = assertThrows(BusinessException.class, () -> postService.create(request));

            // then
            assertEquals(BusinessErrorCode.ITEM_ALREADY_EXIST, result.getErrorCode());
            verify(postRepository).existsByTitle(eq(request.getTitle()));
        }

        @DisplayName("올바르지 않은 Post Type 정책을 가진 Post가 주어지면, 에러가 발생한다.")
        @MethodSource("provideNotValidPostCreateRequest")
        @ParameterizedTest(name = "{index} : {2} 이 주어졌을 때 에러가 발생한다.")
        void create_failWithNotValidPostType(PostCreateRequestServiceDto requestDto, PostType targetPostType, String description) {
            // given

            // 제목이 중복되지 않는지 먼저 검증한다.
            doReturn(false).when(postRepository).existsByTitle(eq(requestDto.getTitle()));

            // 현재 요청한 정보가 해당 포스트 타입의 정책과 일치하는지 검증한다.
            doReturn(Optional.of(targetPostType)).when(postTypeRepository).findById(eq(requestDto.getPostTypeId()));

            // when
            var result = assertThrows(BusinessException.class, () -> postService.create(requestDto));

            // then
            assertEquals(BusinessErrorCode.POLICY_VIOLATE, result.getErrorCode());
            verify(postRepository).existsByTitle(eq(requestDto.getTitle()));
            verify(postTypeRepository).findById(eq(requestDto.getPostTypeId()));
        }

        @DisplayName("시리즈가 필요한 게시글이지만, 시리즈가 존재하지 않는 경우, 에러가 발생한다.")
        @Test
        void create_failWithNeedSeriesButNotExistSeries() {
            // given
            var request = PostCreateRequestServiceDto.builder()
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(1L)
                    .postTypeId(1L)
                    .tagIds(List.of(1L, 2L))
                    .isEnabled(true)
                    .build();

            // 제목이 중복되지 않는지 먼저 검증한다.
            doReturn(false).when(postRepository).existsByTitle(eq(request.getTitle()));

            // 현재 요청한 정보가 해당 포스트 타입의 정책과 일치하는지 검증한다.
            doReturn(Optional.of(createSeriesPostType(true))).when(postTypeRepository).findById(eq(request.getPostTypeId()));

            // 시리즈가 필요한 경우, 시리즈가 존재하는지 검증한다.
            doReturn(Optional.empty()).when(seriesRepository).findById(eq(request.getSeriesId()));

            // when
            var result = assertThrows(BusinessException.class, () -> postService.create(request));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(postRepository).existsByTitle(eq(request.getTitle()));
            verify(postTypeRepository).findById(eq(request.getPostTypeId()));
            verify(seriesRepository).findById(eq(request.getSeriesId()));
        }

        @DisplayName("올바르지 않은 Tag ID가 주어지면, 에러가 발생한다.")
        @Test
        void create_failWithNotValidTagId() {
            // given
            var request = PostCreateRequestServiceDto.builder()
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(1L)
                    .postTypeId(1L)
                    .tagIds(List.of(1L, 2L))
                    .isEnabled(true)
                    .build();

            // 제목이 중복되지 않는지 먼저 검증한다.
            doReturn(false).when(postRepository).existsByTitle(eq(request.getTitle()));

            // 현재 요청한 정보가 해당 포스트 타입의 정책과 일치하는지 검증한다.
            doReturn(Optional.of(createSeriesPostType(true))).when(postTypeRepository).findById(eq(request.getPostTypeId()));

            // 시리즈가 필요한 경우, 시리즈가 존재하는지 검증한다.
            doReturn(Optional.of(createSeries(request.getSeriesId()))).when(seriesRepository).findById(eq(request.getSeriesId()));

            // 본문을 생성한다.
            var plainText = "content";

            doReturn(plainText).when(markdownHelper).toPlainText(eq(request.getContent()));

            var targetContent = Content.builder().plainText(plainText).editorText(request.getContent()).build();
            var savedContent = createContent(targetContent.getEditorText(), targetContent.getPlainText());
            doReturn(savedContent).when(contentRepository).save(eq(targetContent));

            // 포스트를 저장한다.
            var targetPost = Post.builder()
                    .title(request.getTitle())
                    .content(targetContent)
                    .thumbnail(request.getThumbnail())
                    .postType(createSeriesPostType(true))
                    .isEnabled(request.isEnabled())
                    .build();
            targetPost.setSeries(createSeries(request.getSeriesId()));

            var savedPost = createFullyPost(1L);
            savedPost.setSeries(createSeries(request.getSeriesId()));
            savedPost.setPostType(createSeriesPostType(true));
            savedPost.setContent(savedContent);
            savedPost.setTitle(request.getTitle());
            savedPost.setThumbnail(request.getThumbnail());
            savedPost.setEnabled(request.isEnabled());

            doReturn(savedPost).when(postRepository).save(eq(targetPost));

            // 태그를 검증하고, 태그를 등록한다.

            var tags = List.of(
                    createTag(1L, "1"),
                    createTag(2L, "2")
            );

            doReturn(List.of(tags.get(0))).when(tagRepository).findTagsByIdIn(eq(request.getTagIds()));

            // when
            var result = assertThrows(BusinessException.class, () -> postService.create(request));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(postRepository).existsByTitle(eq(request.getTitle()));
            verify(postTypeRepository).findById(eq(request.getPostTypeId()));
            verify(seriesRepository).findById(eq(request.getSeriesId()));
            verify(markdownHelper).toPlainText(eq(request.getContent()));
            verify(contentRepository).save(eq(targetContent));
            verify(postRepository).save(eq(targetPost));
            verify(tagRepository).findTagsByIdIn(eq(request.getTagIds()));
        }
    }

    @DisplayName("PostService.getAllByTitleKeyword")
    @Nested
    class getAllByTitleKeyword {

        @DisplayName("올바른 키워드로 조회할 경우, 해당 키워드를 포함하는 Post를 반환한다.")
        @Test
        void getAllByTitleKeyword_successWithValidKeyword() {
            // given
            var validKeyword = "전역";

            var targetPost = createFullyPostWithTitle(validKeyword + "하고 싶다");

            doReturn(createPageObject(targetPost)).when(postRepository).matchByTitle(eq(validKeyword), any(MultiValueMap.class), any(Pageable.class));

            // when
            var result = postService.getAllByTitleKeyword(validKeyword, getMockPostQueryParam(), getMockPageable());

            // then
            var expected = PostSimpleResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .thumbnail(targetPost.getThumbnail())
                    .series(SeriesSimpleResponseDto.builder()
                            .id(targetPost.getSeries().getId())
                            .name(targetPost.getSeries().getName())
                            .build())
                    .tags(targetPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // then
            assertEquals(1, result.getTotalElements());
            assertEquals(expected, result.getContent().get(0));
            verify(postRepository).matchByTitle(eq(validKeyword), any(MultiValueMap.class), any(Pageable.class));
        }

    }

    @DisplayName("PostService.getAllByContentKeyword")
    @Nested
    class getAllByContentKeyword {
        @DisplayName("올바른 키워드로 조회할 경우, 해당 키워드를 포함하는 Post를 반환한다.")
        @Test
        void getAllByContentKeyword_successWithValidKeyword() {
            // given
            var validKeyword = "전역";

            var targetPost = createFullyPost(1L);
            targetPost.setContent(createContent(validKeyword + "하고 싶다", validKeyword + "하고 싶다"));

            doReturn(createPageObject(targetPost)).when(postRepository).matchByContent(eq(validKeyword), any(MultiValueMap.class), any(Pageable.class));

            // when
            var result = postService.getAllByContentKeyword(validKeyword, getMockPostQueryParam(), getMockPageable());

            // then
            var expected = PostSimpleResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .thumbnail(targetPost.getThumbnail())
                    .series(SeriesSimpleResponseDto.builder()
                            .id(targetPost.getSeries().getId())
                            .name(targetPost.getSeries().getName())
                            .build())
                    .tags(targetPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // then
            assertEquals(1, result.getTotalElements());
            assertEquals(expected, result.getContent().get(0));
            verify(postRepository).matchByContent(eq(validKeyword), any(MultiValueMap.class), any(Pageable.class));
        }
    }

    @DisplayName("PostService.getAllByMixKeyword")
    @Nested
    class getAllByMixKeyword {
        @DisplayName("올바른 키워드로 조회할 경우, 해당 키워드를 포함하는 Post를 반환한다.")
        @Test
        void getAllByMixKeyword_successWithValidKeyword() {
            // given
            var validKeyword = "전역";

            var targetPost = createFullyPostWithTitle(validKeyword + "하고 싶다");
            targetPost.setContent(createContent(validKeyword + "하고 싶다", validKeyword + "하고 싶다"));

            doReturn(createPageObject(targetPost)).when(postRepository).matchByMix(eq(validKeyword), any(MultiValueMap.class), any(Pageable.class));

            // when
            var result = postService.getAllByMixKeyword(validKeyword, getMockPostQueryParam(), getMockPageable());

            // then
            var expected = PostSimpleResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .thumbnail(targetPost.getThumbnail())
                    .series(SeriesSimpleResponseDto.builder()
                            .id(targetPost.getSeries().getId())
                            .name(targetPost.getSeries().getName())
                            .build())
                    .tags(targetPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // then
            assertEquals(1, result.getTotalElements());
            assertEquals(expected, result.getContent().get(0));
            verify(postRepository).matchByMix(eq(validKeyword), any(MultiValueMap.class), any(Pageable.class));
        }
    }

    @DisplayName("PostService.getAllByQueryParams")
    @Nested
    class getAllByQueryParams {
        @DisplayName("올바른 쿼리로 조회할 경우, 해당 쿼리를 만족하는 Post를 반환한다.")
        @Test
        void getAllByQueryParams_successWithValidQuery() {
            // given
            var targetPost = createFullyPost(1L);


            doReturn(createPageObject(targetPost)).when(postRepository).findAllPostWithQueryParam(any(MultiValueMap.class), any(Pageable.class));

            // when
            var result = postService.getAllByQueryParams(getMockPostQueryParam(), getMockPageable());

            // then
            var expected = PostSimpleResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .thumbnail(targetPost.getThumbnail())
                    .series(SeriesSimpleResponseDto.builder()
                            .id(targetPost.getSeries().getId())
                            .name(targetPost.getSeries().getName())
                            .build())
                    .tags(targetPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // then
            assertEquals(1, result.getTotalElements());
            assertEquals(expected, result.getContent().get(0));
            verify(postRepository).findAllPostWithQueryParam(any(MultiValueMap.class), any(Pageable.class));
        }
    }

    @DisplayName("PostService.update")
    @Nested
    class update {

        @DisplayName("새로운 제목이 주어지면, 해당 제목으로 업데이트한다.")
        @Test
        void update_successTitle() {
            // given
            var targetPost = createFullyPost(1L);
            targetPost.setPostType(createSeriesPostType(false));
            targetPost.setSeries(null);

            var requestDto = PostUpdateRequestServiceDto.builder()
                    .id(1L)
                    .title("new title")
                    .content(targetPost.getContent().getEditorText())
                    .thumbnail(targetPost.getThumbnail())
                    .seriesId(null)
                    .tagIds(targetPost.getPostTags().stream().map(postTag -> postTag.getTag().getId()).toList())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // post 를 찾는다.
            doReturn(Optional.of(targetPost)).when(postRepository).findById(eq(requestDto.getId()));

            // 게시글 제목 수정
            doReturn(false).when(postRepository).existsByTitle(eq(requestDto.getTitle()));

            // 태그 변경 없음
            var tagComparatorResult = new TagComparisonResult<PostTag>();
            doReturn(tagComparatorResult).when(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());

            var updatedPost = PostTestDataFactory.copyPost(targetPost);
            updatedPost.setTitle(requestDto.getTitle());

            doReturn(updatedPost).when(postRepository).save(eq(updatedPost));

            // when
            var result = postService.update(requestDto);

            // then
            var expected = PostDetailResponseCommonDto.builder()
                    .id(1L)
                    .title(requestDto.getTitle())
                    .content(ContentResponseControllerDto.builder()
                            .editorText(targetPost.getContent().getEditorText())
                            .plainText(targetPost.getContent().getPlainText())
                            .build())
                    .thumbnail(targetPost.getThumbnail())
                    .series(null)
                    .tags(targetPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            assertEquals(expected, result);
            verify(postRepository).findById(eq(requestDto.getId()));
            verify(postRepository).existsByTitle(eq(requestDto.getTitle()));
            verify(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());
            verify(postRepository).save(eq(updatedPost));
            verifyNoInteractions(markdownHelper);
            verifyNoInteractions(contentRepository);
            verifyNoInteractions(tagRepository);
            verifyNoInteractions(postTagRepository);
            verifyNoInteractions(seriesRepository);
        }

        @DisplayName("새로운 본문이 주어지면, 해당 본문으로 업데이트한다.")
        @Test
        void update_successContent() {
            // given
            var targetPost = createFullyPost(1L);
            targetPost.setPostType(createSeriesPostType(false));
            targetPost.setSeries(null);

            var requestDto = PostUpdateRequestServiceDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content("new content")
                    .thumbnail(targetPost.getThumbnail())
                    .seriesId(null)
                    .tagIds(targetPost.getPostTags().stream().map(postTag -> postTag.getTag().getId()).toList())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // post 를 찾는다.
            doReturn(Optional.of(targetPost)).when(postRepository).findById(eq(requestDto.getId()));

            // 본문 수정
            var plainText = "new content";

            doReturn(plainText).when(markdownHelper).toPlainText(eq(requestDto.getContent()));

            var targetContent = targetPost.getContent();
            var updatedContent = copyContent(targetContent);
            updatedContent.setPlainText(plainText);
            updatedContent.setEditorText(requestDto.getContent());

            doReturn(updatedContent).when(contentRepository).save(eq(updatedContent));

            // 태그 변경 없음
            var tagComparatorResult = new TagComparisonResult<PostTag>();
            doReturn(tagComparatorResult).when(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());

            var updatedPost = PostTestDataFactory.copyPost(targetPost);
            updatedPost.setContent(updatedContent);

            doReturn(updatedPost).when(postRepository).save(eq(updatedPost));

            // when
            var result = postService.update(requestDto);

            // then
            var expected = PostDetailResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(ContentResponseControllerDto.builder()
                            .editorText(requestDto.getContent())
                            .plainText(plainText)
                            .build())
                    .thumbnail(targetPost.getThumbnail())
                    .series(null)
                    .tags(targetPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            assertEquals(expected, result);
            verify(postRepository).findById(eq(requestDto.getId()));
            verify(markdownHelper).toPlainText(eq(requestDto.getContent()));
            verify(contentRepository).save(eq(targetContent));
            verify(postRepository).save(eq(updatedPost));
            verifyNoInteractions(tagRepository);
            verifyNoInteractions(postTagRepository);
            verifyNoInteractions(seriesRepository);
        }

        @DisplayName("태그가 추가되면, 해당 태그를 추가한다.")
        @Test
        void update_successAddTag() {
            // given
            var targetPost = createFullyPost(1L);
            targetPost.setPostType(createSeriesPostType(false));
            targetPost.setSeries(null);

            var requestDto = PostUpdateRequestServiceDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(targetPost.getContent().getEditorText())
                    .thumbnail(targetPost.getThumbnail())
                    .seriesId(null)
                    .tagIds(List.of(1L, 2L, 3L, 4L))
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // post 를 찾는다.
            doReturn(Optional.of(targetPost)).when(postRepository).findById(eq(requestDto.getId()));

            // 4L 태그 추가
            var tagComparatorResult = new TagComparisonResult<PostTag>();
            tagComparatorResult.setAddedTag(List.of(4L));
            tagComparatorResult.setDiffCnt(1);
            doReturn(tagComparatorResult).when(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());

            // 변경할 태그 찾기
            var addedTag = createTag(4L, "4");
            doReturn(List.of(addedTag)).when(tagRepository).findTagsByIdIn(eq(List.of(4L)));

            var addedPostTag = PostTag.builder().tag(addedTag).post(targetPost).build();
            var savedPostTag = copyPostTag(addedPostTag);
            savedPostTag.setId(4L);

            doReturn(List.of(savedPostTag)).when(postTagRepository).saveAll(eq(List.of(addedPostTag)));

            var updatedPost = PostTestDataFactory.copyPost(targetPost);
            updatedPost.addPostTag(savedPostTag);

            doReturn(updatedPost).when(postRepository).save(eq(updatedPost));

            // when
            var result = postService.update(requestDto);

            // then
            var expected = PostDetailResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(ContentResponseControllerDto.builder()
                            .editorText(targetPost.getContent().getEditorText())
                            .plainText(targetPost.getContent().getPlainText())
                            .build())
                    .thumbnail(targetPost.getThumbnail())
                    .series(null)
                    .tags(updatedPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            assertEquals(expected, result);
            verify(postRepository).findById(eq(requestDto.getId()));
            verify(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());
            verify(tagRepository).findTagsByIdIn(eq(List.of(4L)));
            verify(postTagRepository).saveAll(eq(List.of(addedPostTag)));
            verify(postRepository).save(eq(updatedPost));
            verifyNoInteractions(markdownHelper);
            verifyNoInteractions(contentRepository);
            verifyNoInteractions(seriesRepository);

        }

        @DisplayName("태그가 삭제되면, 해당 태그를 삭제한다.")
        @Test
        void update_successRemoveTag() {
            // given
            var targetPost = createFullyPost(1L);
            targetPost.setPostType(createSeriesPostType(false));
            targetPost.setSeries(null);

            var requestDto = PostUpdateRequestServiceDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(targetPost.getContent().getEditorText())
                    .thumbnail(targetPost.getThumbnail())
                    .seriesId(null)
                    .tagIds(List.of(1L, 3L))
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // post 를 찾는다.
            doReturn(Optional.of(targetPost)).when(postRepository).findById(eq(requestDto.getId()));

            // 2L 태그 삭제
            var removeTag = targetPost.getPostTags().stream().filter(postTag -> postTag.getTag().getId().equals(2L)).findFirst().orElseThrow();

            var tagComparatorResult = new TagComparisonResult<PostTag>();
            tagComparatorResult.setRemovedTag(List.of(removeTag));
            tagComparatorResult.setDiffCnt(1);
            doReturn(tagComparatorResult).when(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());

            // 변경할 태그 찾기
            doNothing().when(postTagRepository).deleteAllById(eq(List.of(removeTag.getId())));

            var updatedPost = PostTestDataFactory.copyPost(targetPost);
            updatedPost.deletePostTag(List.of(removeTag));

            doReturn(updatedPost).when(postRepository).save(eq(updatedPost));

            // when
            var result = postService.update(requestDto);

            // then
            var expected = PostDetailResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(ContentResponseControllerDto.builder()
                            .editorText(targetPost.getContent().getEditorText())
                            .plainText(targetPost.getContent().getPlainText())
                            .build())
                    .thumbnail(targetPost.getThumbnail())
                    .series(null)
                    .tags(updatedPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            assertEquals(expected, result);
            verify(postRepository).findById(eq(requestDto.getId()));
            verify(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());
            verify(postTagRepository).deleteAllById(eq(List.of(removeTag.getId())));
            verify(postRepository).save(eq(updatedPost));
            verifyNoInteractions(markdownHelper);
            verifyNoInteractions(contentRepository);
            verifyNoInteractions(seriesRepository);
        }

        @DisplayName("태그가 변경되면, 추가된 태그를 추가하고, 삭제된 태그를 삭제한다.")
        @Test
        void update_successChangeTag() {
            // given
            var targetPost = createFullyPost(1L);
            targetPost.setPostType(createSeriesPostType(false));
            targetPost.setSeries(null);

            var requestDto = PostUpdateRequestServiceDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(targetPost.getContent().getEditorText())
                    .thumbnail(targetPost.getThumbnail())
                    .seriesId(null)
                    .tagIds(List.of(1L, 3L, 4L))
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // post 를 찾는다.
            doReturn(Optional.of(targetPost)).when(postRepository).findById(eq(requestDto.getId()));

            // 2L 태그 삭제, 4L 태그 추가
            var removeTag = targetPost.getPostTags().stream().filter(postTag -> postTag.getTag().getId().equals(2L)).findFirst().orElseThrow();

            var tagComparatorResult = new TagComparisonResult<PostTag>();
            tagComparatorResult.setAddedTag(List.of(4L));
            tagComparatorResult.setRemovedTag(List.of(removeTag));
            tagComparatorResult.setDiffCnt(2);
            doReturn(tagComparatorResult).when(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());

            // 변경할 태그 찾기
            var addedTag = createTag(4L, "4");
            doReturn(List.of(addedTag)).when(tagRepository).findTagsByIdIn(eq(List.of(4L)));

            var addedPostTag = PostTag.builder().tag(addedTag).post(targetPost).build();
            var savedPostTag = copyPostTag(addedPostTag);
            savedPostTag.setId(4L);

            doReturn(List.of(savedPostTag)).when(postTagRepository).saveAll(eq(List.of(addedPostTag)));
            doNothing().when(postTagRepository).deleteAllById(eq(List.of(removeTag.getId())));

            var updatedPost = PostTestDataFactory.copyPost(targetPost);
            updatedPost.deletePostTag(List.of(removeTag));
            updatedPost.addPostTag(savedPostTag);

            doReturn(updatedPost).when(postRepository).save(eq(updatedPost));

            // when
            var result = postService.update(requestDto);

            // then
            var expected = PostDetailResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(ContentResponseControllerDto.builder()
                            .editorText(targetPost.getContent().getEditorText())
                            .plainText(targetPost.getContent().getPlainText())
                            .build())
                    .thumbnail(targetPost.getThumbnail())
                    .series(null)
                    .tags(updatedPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            assertEquals(expected, result);
            verify(postRepository).findById(eq(requestDto.getId()));
            verify(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());
            verify(tagRepository).findTagsByIdIn(eq(List.of(4L)));
            verify(postTagRepository).saveAll(eq(List.of(addedPostTag)));
            verify(postTagRepository).deleteAllById(eq(List.of(removeTag.getId())));
            verify(postRepository).save(eq(updatedPost));
            verifyNoInteractions(markdownHelper);
            verifyNoInteractions(contentRepository);
            verifyNoInteractions(seriesRepository);
        }

        @DisplayName("시리즈가 필요한 게시글에서 새로운 시리즈가 주어지면, 해당 시리즈로 업데이트한다.")
        @Test
        void update_successSeries() {
            // given
            var targetPost = createFullyPost(1L);
            targetPost.setPostType(createSeriesPostType(true));
            targetPost.setSeries(createSeries(1L));

            var requestDto = PostUpdateRequestServiceDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(targetPost.getContent().getEditorText())
                    .thumbnail(targetPost.getThumbnail())
                    .seriesId(2L)
                    .tagIds(targetPost.getPostTags().stream().map(postTag -> postTag.getTag().getId()).toList())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            // post 를 찾는다.
            doReturn(Optional.of(targetPost)).when(postRepository).findById(eq(requestDto.getId()));

            // 태그 변경 없음
            var tagComparatorResult = new TagComparisonResult<PostTag>();
            doReturn(tagComparatorResult).when(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());

            // 시리즈 업데이트
            doReturn(Optional.of(createSeries(requestDto.getSeriesId()))).when(seriesRepository).findById(eq(requestDto.getSeriesId()));

            var updatedPost = PostTestDataFactory.copyPost(targetPost);
            updatedPost.setSeries(createSeries(requestDto.getSeriesId()));

            doReturn(updatedPost).when(postRepository).save(eq(updatedPost));

            // when
            var result = postService.update(requestDto);

            // then
            var expected = PostDetailResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(ContentResponseControllerDto.builder()
                            .editorText(targetPost.getContent().getEditorText())
                            .plainText(targetPost.getContent().getPlainText())
                            .build())
                    .thumbnail(targetPost.getThumbnail())
                    .series(SeriesSimpleResponseDto.builder()
                            .id(updatedPost.getSeries().getId())
                            .name(updatedPost.getSeries().getName())
                            .build())
                    .tags(targetPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(targetPost.isEnabled())
                    .build();

            assertEquals(expected, result);
            verify(postRepository).findById(eq(requestDto.getId()));
            verify(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());
            verify(seriesRepository).findById(eq(requestDto.getSeriesId()));
            verify(postRepository).save(eq(updatedPost));
            verifyNoInteractions(markdownHelper);
            verifyNoInteractions(contentRepository);
            verifyNoInteractions(tagRepository);
        }

        @DisplayName("새로운 썸네일과 활성화 여부가 주어지면, 해당 정보로 업데이트한다.")
        @Test
        void update_successThumbnailAndEnabled() {
            // given
            var targetPost = createFullyPost(1L);
            targetPost.setPostType(createSeriesPostType(false));
            targetPost.setSeries(null);

            var requestDto = PostUpdateRequestServiceDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(targetPost.getContent().getEditorText())
                    .thumbnail("new thumbnail")
                    .seriesId(null)
                    .tagIds(targetPost.getPostTags().stream().map(postTag -> postTag.getTag().getId()).toList())
                    .isEnabled(false)
                    .build();

            // post 를 찾는다.
            doReturn(Optional.of(targetPost)).when(postRepository).findById(eq(requestDto.getId()));

            // 태그 변경 없음
            var tagComparatorResult = new TagComparisonResult<PostTag>();
            doReturn(tagComparatorResult).when(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());

            var updatedPost = PostTestDataFactory.copyPost(targetPost);
            updatedPost.setThumbnail(requestDto.getThumbnail());
            updatedPost.setEnabled(requestDto.isEnabled());

            doReturn(updatedPost).when(postRepository).save(eq(updatedPost));

            // when
            var result = postService.update(requestDto);

            // then
            var expected = PostDetailResponseCommonDto.builder()
                    .id(1L)
                    .title(targetPost.getTitle())
                    .content(ContentResponseControllerDto.builder()
                            .editorText(targetPost.getContent().getEditorText())
                            .plainText(targetPost.getContent().getPlainText())
                            .build())
                    .thumbnail(requestDto.getThumbnail())
                    .series(null)
                    .tags(targetPost.getPostTags().stream().map(
                            postTag -> TagResponseDto.builder().id(postTag.getTag().getId()).name(postTag.getTag().getName()).build()
                    ).toList())
                    .postType(PostTypeResponseDto.builder()
                            .id(targetPost.getPostType().getId())
                            .name(targetPost.getPostType().getName())
                            .build())
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .isEnabled(requestDto.isEnabled())
                    .build();

            assertEquals(expected, result);
            verify(postRepository).findById(eq(requestDto.getId()));
            verify(tagComparator).getTagCompareResult(targetPost.getPostTags(), requestDto.getTagIds());
            verify(postRepository).save(eq(updatedPost));
            verifyNoInteractions(markdownHelper);
            verifyNoInteractions(contentRepository);
            verifyNoInteractions(tagRepository);
            verifyNoInteractions(seriesRepository);
        }
    }

    @DisplayName("PostService.delete")
    @Nested
    class delete {
    }

    @DisplayName("PostService.updateStatus")
    @Nested
    class updateStatus {
    }

    @DisplayName("PostService.isExistTitle")
    @Nested
    class isExistTitle {
        @DisplayName("올바른 제목이 주어지면, 해당 제목을 가진 Post가 존재하는지 반환한다.")
        @Test
        void isExistTitle_successWithValidTitle() {
            // given
            var validTitle = "title";

            doReturn(true).when(postRepository).existsByTitle(eq(validTitle));

            // when
            var result = postService.isExistTitle(validTitle);

            // then
            assertTrue(result);
            verify(postRepository).existsByTitle(eq(validTitle));
        }

        @DisplayName("올바르지 않은 제목이 주어지면, 해당 제목을 가진 Post가 존재하지 않는지 반환한다.")
        @Test
        void isExistTitle_failWithInvalidTitle() {
            // given
            var invalidTitle = "title";

            doReturn(false).when(postRepository).existsByTitle(eq(invalidTitle));

            // when
            var result = postService.isExistTitle(invalidTitle);

            // then
            assertFalse(result);
            verify(postRepository).existsByTitle(eq(invalidTitle));
        }
    }
}

