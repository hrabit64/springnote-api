package com.springnote.api.tests.service;

import com.springnote.api.domain.postType.PostTypeRepository;
import com.springnote.api.domain.series.Series;
import com.springnote.api.domain.series.SeriesRepository;
import com.springnote.api.domain.tag.TagRepository;
import com.springnote.api.domain.tmpPost.TmpPost;
import com.springnote.api.domain.tmpPost.TmpPostRepository;
import com.springnote.api.domain.tmpPostTag.TmpPostTag;
import com.springnote.api.domain.tmpPostTag.TmpPostTagRepository;
import com.springnote.api.dto.post.service.PostCreateRequestServiceDto;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.dto.series.common.SeriesSimpleResponseDto;
import com.springnote.api.dto.tag.common.TagResponseDto;
import com.springnote.api.dto.tmpPost.common.TmpPostResponseCommonDto;
import com.springnote.api.dto.tmpPost.service.TmpPostCreateRequestServiceDto;
import com.springnote.api.dto.tmpPost.service.TmpPostUpdateRequestServiceDto;
import com.springnote.api.service.PostService;
import com.springnote.api.service.TmpPostService;
import com.springnote.api.testUtils.dataFactory.TestDataFactory;
import com.springnote.api.testUtils.dataFactory.series.SeriesTestDataFactory;
import com.springnote.api.testUtils.dataFactory.tmpPost.TmpPostDataFactory;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
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

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.getMockPageable;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static com.springnote.api.testUtils.dataFactory.postTag.TmpPostTagTestDataFactory.createTmpPostTag;
import static com.springnote.api.testUtils.dataFactory.postType.PostTypeTestDataFactory.createSeriesPostType;
import static com.springnote.api.testUtils.dataFactory.tag.TagTestDataFactory.createTag;
import static com.springnote.api.testUtils.dataFactory.tmpPost.TmpPostDataFactory.copyTmpPost;
import static com.springnote.api.testUtils.dataFactory.tmpPost.TmpPostDataFactory.createTmpPost;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@DisplayName("Service Test - TmpPostService")
public class TmpPostServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private TmpPostService tmpPostService;

    @Mock
    private TmpPostRepository tmpPostRepository;

    @Mock
    private TmpPostTagRepository tmpPostTagRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private SeriesRepository seriesRepository;

    @Mock
    private PostTypeRepository postTypeRepository;

    @Mock
    private TagComparator<TmpPostTag> tagComparator;

    @Mock
    private PostService postService;

    @DisplayName("TmpPostService.getById")
    @Nested
    class getById {
        @DisplayName("올바른 임시 포스트 ID를 입력하면 해당 임시 포스트를 반환한다.")
        @Test
        void getById_success() {
            // given
            var validId = "validId";
            var targetTmpPost = createTmpPost(validId);
            doReturn(Optional.of(targetTmpPost)).when(tmpPostRepository).findPostById(eq(validId));

            // when
            var result = tmpPostService.getById(validId);

            // then
            var expected = TmpPostResponseCommonDto.builder()
                    .id(targetTmpPost.getId())
                    .title(targetTmpPost.getTitle())
                    .content(targetTmpPost.getContent())
                    .thumbnail(targetTmpPost.getThumbnail())
                    .series(new SeriesSimpleResponseDto(targetTmpPost.getSeries()))
                    .postType(new PostTypeResponseDto(targetTmpPost.getPostType()))
                    .createdAt(targetTmpPost.getCreatedDate())
                    .lastUpdatedAt(targetTmpPost.getLastModifiedDate())
                    .tags(targetTmpPost.getTmpPostTags().stream().map(tmpPostTag -> new TagResponseDto(tmpPostTag.getTag())).toList())
                    .build();

            assertEquals(expected, result);
            verify(tmpPostRepository).findPostById(eq(validId));
        }

        @DisplayName("존재하지 않는 임시 포스트 ID를 입력하면 BusinessException을 던진다.")
        @Test
        void getById_fail() {
            // given
            var invalidId = "invalidId";
            doReturn(Optional.empty()).when(tmpPostRepository).findPostById(eq(invalidId));

            // when
            var exception = assertThrows(BusinessException.class, () -> tmpPostService.getById(invalidId));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, exception.getErrorCode());
            verify(tmpPostRepository).findPostById(eq(invalidId));
        }
    }

    @DisplayName("TmpPostService.getAll")
    @Nested
    class getAll {

        @DisplayName("모든 임시 포스트를 반환한다.")
        @Test
        void getAll_success() {
            // given
            var targetTmpPost = createTmpPost("1");
            var pagedTmpPost = TestDataFactory.createPageObject(targetTmpPost);
            doReturn(pagedTmpPost).when(tmpPostRepository).findAllBy(any(Pageable.class));

            // when
            var result = tmpPostService.getAll(getMockPageable());

            // then
            var expected = TmpPostResponseCommonDto
                    .builder()
                    .id(targetTmpPost.getId())
                    .title(targetTmpPost.getTitle())
                    .content(targetTmpPost.getContent())
                    .thumbnail(targetTmpPost.getThumbnail())
                    .series(new SeriesSimpleResponseDto(targetTmpPost.getSeries()))
                    .postType(new PostTypeResponseDto(targetTmpPost.getPostType()))
                    .createdAt(targetTmpPost.getCreatedDate())
                    .lastUpdatedAt(targetTmpPost.getLastModifiedDate())
                    .tags(targetTmpPost.getTmpPostTags().stream().map(tmpPostTag -> new TagResponseDto(tmpPostTag.getTag())).toList())
                    .build();

            assertEquals(1, result.getTotalElements());
            assertEquals(expected, result.getContent().get(0));
            verify(tmpPostRepository).findAllBy(any(Pageable.class));
        }

    }


    @DisplayName("TmpPostService.create")
    @Nested
    class create {


        @DisplayName("임시 포스트를 생성한다.")
        @Test
        void create_success() {
            // given
            var requestDto = TmpPostCreateRequestServiceDto.builder()
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(null)
                    .postTypeId(1L)
                    .tagIds(List.of(1L, 2L))
                    .build();
            var isNeedSeries = requestDto.getSeriesId() != null;
            var targetPostType = createSeriesPostType(isNeedSeries);

            doReturn(Optional.of(targetPostType)).when(postTypeRepository).findById(eq(requestDto.getPostTypeId()));

            Series targetSeries = null;

            if (isNeedSeries) {
                targetSeries = SeriesTestDataFactory.createSeries(requestDto.getSeriesId());
                doReturn(Optional.of(targetSeries)).when(seriesRepository).findById(eq(requestDto.getSeriesId()));
            }

            // 태그 검증
            var targetTags = List.of(
                    createTag(1L),
                    createTag(2L)
            );

            doReturn(targetTags).when(tagRepository).findTagsByIdIn(eq(requestDto.getTagIds()));

            var targetTmpPost = TmpPost
                    .builder()
                    .title(requestDto.getTitle())
                    .content(requestDto.getContent())
                    .thumbnail(requestDto.getThumbnail())
                    .series(isNeedSeries ? targetSeries : null)
                    .postType(targetPostType)
                    .createdDate(testLocalDateTime())
                    .lastModifiedDate(testLocalDateTime())
                    .build();

            var savedTmpPost = copyTmpPost(targetTmpPost);
            savedTmpPost.setId("savedId");

            doReturn(savedTmpPost).when(tmpPostRepository).save(targetTmpPost);

            // 태그 추가
            var tmpPostTags = targetTags.stream().map(tag -> createTmpPostTag(savedTmpPost, tag)).toList();
            List<TmpPostTag> savedTmpPostTags = tmpPostTags.stream().map(tmpPostTag -> {
                var savedTmpPostTag = createTmpPostTag(savedTmpPost, tmpPostTag.getTag());
                savedTmpPostTag.setId(tmpPostTag.getId());
                return savedTmpPostTag;
            }).toList();

            doReturn(savedTmpPostTags).when(tmpPostTagRepository).saveAll(eq(tmpPostTags));

            // when
            var result = tmpPostService.create(requestDto);

            // then
            var expected = TmpPostResponseCommonDto.builder()
                    .id(savedTmpPost.getId())
                    .title(targetTmpPost.getTitle())
                    .content(targetTmpPost.getContent())
                    .thumbnail(targetTmpPost.getThumbnail())
                    .series((isNeedSeries) ? new SeriesSimpleResponseDto(targetTmpPost.getSeries()) : null)
                    .postType(new PostTypeResponseDto(targetTmpPost.getPostType()))
                    .createdAt(targetTmpPost.getCreatedDate())
                    .lastUpdatedAt(targetTmpPost.getLastModifiedDate())
                    .tags(savedTmpPostTags.stream().map(tmpPostTag -> new TagResponseDto(tmpPostTag.getTag())).toList())
                    .build();

            assertEquals(expected, result);
            verify(tmpPostRepository).save(eq(targetTmpPost));
            verify(tmpPostTagRepository).saveAll(eq(tmpPostTags));
            verify(postTypeRepository).findById(eq(requestDto.getPostTypeId()));
            verify(tagRepository).findTagsByIdIn(eq(requestDto.getTagIds()));
            if (isNeedSeries) verify(seriesRepository).findById(eq(requestDto.getSeriesId()));
        }

    }

    //
    @DisplayName("TmpPostService.update")
    @Nested
    class update {


        @DisplayName("아무런 변경사항이 없는 경우 임시 포스트를 업데이트하지 않는다.")
        @Test
        void update_success() {
            // given
            var request = TmpPostUpdateRequestServiceDto.builder()
                    .id("1")
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(null)
                    .tagIds(List.of(1L, 2L, 3L))
                    .build();
            var targetPost = TmpPostDataFactory.createTmpPost("1");
            targetPost.setTitle("title");
            targetPost.setContent("content");
            targetPost.setThumbnail("thumbnail");
            targetPost.setSeries(null);
            targetPost.setPostType(createSeriesPostType(false));

            doReturn(Optional.of(targetPost)).when(tmpPostRepository).findPostById(eq(request.getId()));

            var tagCompareResult = new TagComparisonResult<TmpPostTag>();
            doReturn(tagCompareResult).when(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));

            doReturn(targetPost).when(tmpPostRepository).save(targetPost);
            // when
            var result = tmpPostService.update(request);

            // then
            var expected = TmpPostResponseCommonDto.builder()
                    .id(targetPost.getId())
                    .title(targetPost.getTitle())
                    .content(targetPost.getContent())
                    .thumbnail(targetPost.getThumbnail())
                    .series((targetPost.getSeries() != null) ? new SeriesSimpleResponseDto(targetPost.getSeries()) : null)
                    .postType(new PostTypeResponseDto(targetPost.getPostType()))
                    .createdAt(targetPost.getCreatedDate())
                    .lastUpdatedAt(targetPost.getLastModifiedDate())
                    .tags(targetPost.getTmpPostTags().stream().map(tmpPostTag -> new TagResponseDto(tmpPostTag.getTag())).toList())
                    .build();

            assertEquals(expected, result);
            verify(tmpPostRepository).findPostById(eq(request.getId()));
            verify(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));
            verify(tmpPostRepository).save(targetPost);
            verifyNoInteractions(tmpPostTagRepository);
            verifyNoInteractions(tagRepository);
            verifyNoInteractions(seriesRepository);

        }

        @DisplayName("새로운 시리즈가 입력된 경우 임시 포스트를 업데이트한다.")
        @Test
        void update_successWithNewSeries() {
            // given
            var request = TmpPostUpdateRequestServiceDto.builder()
                    .id("1")
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(2L)
                    .tagIds(List.of(1L, 2L, 3L))
                    .build();

            var targetPost = TmpPostDataFactory.createTmpPost("1");
            targetPost.setTitle("title");
            targetPost.setContent("content");
            targetPost.setThumbnail("thumbnail");
            targetPost.setPostType(createSeriesPostType(true));

            doReturn(Optional.of(targetPost)).when(tmpPostRepository).findPostById(eq(request.getId()));

            var tagCompareResult = new TagComparisonResult<TmpPostTag>();
            doReturn(tagCompareResult).when(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));

            var newSeries = SeriesTestDataFactory.createSeries(2L);
            doReturn(Optional.of(newSeries)).when(seriesRepository).findById(eq(request.getSeriesId()));

            var savedPost = copyTmpPost(targetPost);
            savedPost.setSeries(newSeries);
            doReturn(savedPost).when(tmpPostRepository).save(targetPost);

            // when
            var result = tmpPostService.update(request);

            // then
            var expected = TmpPostResponseCommonDto.builder()
                    .id(savedPost.getId())
                    .title(savedPost.getTitle())
                    .content(savedPost.getContent())
                    .thumbnail(savedPost.getThumbnail())
                    .series(new SeriesSimpleResponseDto(savedPost.getSeries()))
                    .postType(new PostTypeResponseDto(savedPost.getPostType()))
                    .createdAt(savedPost.getCreatedDate())
                    .lastUpdatedAt(savedPost.getLastModifiedDate())
                    .tags(savedPost.getTmpPostTags().stream().map(tmpPostTag -> new TagResponseDto(tmpPostTag.getTag())).toList())
                    .build();

            assertEquals(expected, result);
            verify(tmpPostRepository).findPostById(eq(request.getId()));
            verify(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));
            verify(seriesRepository).findById(eq(request.getSeriesId()));
            verify(tmpPostRepository).save(targetPost);
            verifyNoInteractions(tmpPostTagRepository);
            verifyNoInteractions(tagRepository);
        }

        @DisplayName("태그가 추가 된 경우 임시 포스트를 업데이트한다.")
        @Test
        void update_successWithNewTag() {
            // given
            var request = TmpPostUpdateRequestServiceDto.builder()
                    .id("1")
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(null)
                    .tagIds(List.of(1L, 2L, 3L, 4L))
                    .build();

            var targetPost = TmpPostDataFactory.createTmpPost("1");
            targetPost.setTitle("title");
            targetPost.setContent("content");
            targetPost.setThumbnail("thumbnail");

            doReturn(Optional.of(targetPost)).when(tmpPostRepository).findPostById(eq(request.getId()));

            var tagCompareResult = new TagComparisonResult<TmpPostTag>();
            tagCompareResult.setAddedTag(List.of(4L));
            tagCompareResult.setDiffCnt(1);
            doReturn(tagCompareResult).when(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));

            var newTag = createTag(4L);
            doReturn(List.of(newTag)).when(tagRepository).findTagsByIdIn(eq(List.of(4L)));

            var newTmpPostTag = createTmpPostTag(targetPost, newTag);
            doReturn(List.of(newTmpPostTag)).when(tmpPostTagRepository).saveAll(eq(List.of(newTmpPostTag)));

            var savedPost = copyTmpPost(targetPost);
            savedPost.addTmpPostTag(List.of(newTmpPostTag));
            doReturn(savedPost).when(tmpPostRepository).save(targetPost);

            // when
            var result = tmpPostService.update(request);

            // then
            var expected = TmpPostResponseCommonDto.builder()
                    .id(savedPost.getId())
                    .title(savedPost.getTitle())
                    .content(savedPost.getContent())
                    .thumbnail(savedPost.getThumbnail())
                    .series((savedPost.getSeries() != null) ? new SeriesSimpleResponseDto(savedPost.getSeries()) : null)
                    .postType(new PostTypeResponseDto(savedPost.getPostType()))
                    .createdAt(savedPost.getCreatedDate())
                    .lastUpdatedAt(savedPost.getLastModifiedDate())
                    .tags(savedPost.getTmpPostTags().stream().map(tmpPostTag -> new TagResponseDto(tmpPostTag.getTag())).toList())
                    .build();

            assertEquals(expected, result);
            verify(tmpPostRepository).findPostById(eq(request.getId()));
            verify(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));
            verify(tagRepository).findTagsByIdIn(eq(List.of(4L)));
            verify(tmpPostTagRepository).saveAll(eq(List.of(newTmpPostTag)));
            verify(tmpPostRepository).save(targetPost);
            verifyNoInteractions(seriesRepository);
        }

        @DisplayName("태그가 삭제 된 경우 임시 포스트를 업데이트한다.")
        @Test
        void update_successWithDeletedTag() {
            // given
            var request = TmpPostUpdateRequestServiceDto.builder()
                    .id("1")
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(null)
                    .tagIds(List.of(1L, 3L))
                    .build();

            var targetPost = TmpPostDataFactory.createTmpPost("1");
            targetPost.setTitle("title");
            targetPost.setContent("content");
            targetPost.setThumbnail("thumbnail");

            doReturn(Optional.of(targetPost)).when(tmpPostRepository).findPostById(eq(request.getId()));

            var tagCompareResult = new TagComparisonResult<TmpPostTag>();
            var deletedTag = createTmpPostTag(targetPost, createTag(2L));
            deletedTag.setId(2L);
            tagCompareResult.setRemovedTag(List.of(deletedTag));
            tagCompareResult.setDiffCnt(1);
            doReturn(tagCompareResult).when(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));

            doNothing().when(tmpPostTagRepository).deleteAllById(eq(List.of(2L)));

            var savedPost = copyTmpPost(targetPost);
            savedPost.deleteTmpPostTag(tagCompareResult.getRemovedTag().get(0));
            doReturn(savedPost).when(tmpPostRepository).save(targetPost);

            // when
            var result = tmpPostService.update(request);

            // then
            var expected = TmpPostResponseCommonDto.builder()
                    .id(savedPost.getId())
                    .title(savedPost.getTitle())
                    .content(savedPost.getContent())
                    .thumbnail(savedPost.getThumbnail())
                    .series((savedPost.getSeries() != null) ? new SeriesSimpleResponseDto(savedPost.getSeries()) : null)
                    .postType(new PostTypeResponseDto(savedPost.getPostType()))
                    .createdAt(savedPost.getCreatedDate())
                    .lastUpdatedAt(savedPost.getLastModifiedDate())
                    .tags(savedPost.getTmpPostTags().stream().map(tmpPostTag -> new TagResponseDto(tmpPostTag.getTag())).toList())
                    .build();

            assertEquals(expected, result);
            verify(tmpPostRepository).findPostById(eq(request.getId()));
            verify(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));
            verify(tmpPostRepository).save(targetPost);
            verify(tmpPostTagRepository).deleteAllById(eq(List.of(2L)));
            verifyNoInteractions(seriesRepository);
        }

        @DisplayName("태그가 추가 및 삭제 된 경우 임시 포스트를 업데이트한다.")
        @Test
        void update_successWithNewAndDeletedTag() {
            // given
            var request = TmpPostUpdateRequestServiceDto.builder()
                    .id("1")
                    .title("title")
                    .content("content")
                    .thumbnail("thumbnail")
                    .seriesId(null)
                    .tagIds(List.of(1L, 3L, 4L))
                    .build();

            var targetPost = TmpPostDataFactory.createTmpPost("1");
            targetPost.setTitle("title");
            targetPost.setContent("content");
            targetPost.setThumbnail("thumbnail");

            doReturn(Optional.of(targetPost)).when(tmpPostRepository).findPostById(eq(request.getId()));

            var tagCompareResult = new TagComparisonResult<TmpPostTag>();
            var deletedTag = createTmpPostTag(targetPost, createTag(2L));
            deletedTag.setId(2L);
            tagCompareResult.setRemovedTag(List.of(deletedTag));
            tagCompareResult.setDiffCnt(2);
            tagCompareResult.setAddedTag(List.of(4L));
            doReturn(tagCompareResult).when(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));

            var newTag = createTag(4L);
            doReturn(List.of(newTag)).when(tagRepository).findTagsByIdIn(eq(List.of(4L)));

            var newTmpPostTag = createTmpPostTag(targetPost, newTag);
            doReturn(List.of(newTmpPostTag)).when(tmpPostTagRepository).saveAll(eq(List.of(newTmpPostTag)));

            doNothing().when(tmpPostTagRepository).deleteAllById(eq(List.of(2L)));

            var savedPost = copyTmpPost(targetPost);
            savedPost.deleteTmpPostTag(tagCompareResult.getRemovedTag().get(0));
            savedPost.addTmpPostTag(List.of(newTmpPostTag));
            doReturn(savedPost).when(tmpPostRepository).save(targetPost);

            // when
            var result = tmpPostService.update(request);

            // then
            var expected = TmpPostResponseCommonDto.builder()
                    .id(savedPost.getId())
                    .title(savedPost.getTitle())
                    .content(savedPost.getContent())
                    .thumbnail(savedPost.getThumbnail())
                    .series((savedPost.getSeries() != null) ? new SeriesSimpleResponseDto(savedPost.getSeries()) : null)
                    .postType(new PostTypeResponseDto(savedPost.getPostType()))
                    .createdAt(savedPost.getCreatedDate())
                    .lastUpdatedAt(savedPost.getLastModifiedDate())
                    .tags(savedPost.getTmpPostTags().stream().map(tmpPostTag -> new TagResponseDto(tmpPostTag.getTag())).toList())
                    .build();

            assertEquals(expected, result);
            verify(tmpPostRepository).findPostById(eq(request.getId()));
            verify(tagComparator).getTagCompareResult(eq(targetPost.getTmpPostTags()), eq(request.getTagIds()));
            verify(tagRepository).findTagsByIdIn(eq(List.of(4L)));
            verify(tmpPostTagRepository).saveAll(eq(List.of(newTmpPostTag)));
            verify(tmpPostTagRepository).deleteAllById(eq(List.of(2L)));
            verify(tmpPostRepository).save(targetPost);
            verifyNoInteractions(seriesRepository);
        }
    }

    @DisplayName("TmpPostService.delete")
    @Nested
    class delete {
        @DisplayName("존재하는 임시 포스트 ID를 입력하면 해당 임시 포스트를 삭제한다.")
        @Test
        void delete_success() {
            // given
            var validId = "validId";
            var targetTmpPost = createTmpPost(validId);
            doReturn(Optional.of(targetTmpPost)).when(tmpPostRepository).findPostById(eq(validId));

            // when
            tmpPostService.delete(validId);

            // then
            verify(tmpPostRepository).findPostById(eq(validId));
            verify(tmpPostRepository).delete(eq(targetTmpPost));
        }

        @DisplayName("존재하지 않는 임시 포스트 ID를 입력하면 BusinessException을 던진다.")
        @Test
        void delete_fail() {
            // given
            var invalidId = "invalidId";
            doReturn(Optional.empty()).when(tmpPostRepository).findPostById(eq(invalidId));

            // when
            var exception = assertThrows(BusinessException.class, () -> tmpPostService.delete(invalidId));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, exception.getErrorCode());
            verify(tmpPostRepository).findPostById(eq(invalidId));
            verifyNoInteractions(tmpPostTagRepository);
        }
    }

    @DisplayName("TmpPostService.convertToPost")
    @Nested
    class convertToPost {

        @DisplayName("올바른 임시 포스트 ID를 입력하면 해당 임시 포스트를 게시글로 변환한다.")
        @Test
        void convertToPost_success() {
            // given
            var validId = "validId";
            var targetTmpPost = createTmpPost(validId);
            targetTmpPost.setPostType(createSeriesPostType(true));

            doReturn(Optional.of(targetTmpPost)).when(tmpPostRepository).findPostById(eq(validId));

            var validRequest = PostCreateRequestServiceDto.builder()
                    .title(targetTmpPost.getTitle())
                    .content(targetTmpPost.getContent())
                    .thumbnail(targetTmpPost.getThumbnail())
                    .seriesId(targetTmpPost.getSeries().getId())
                    .postTypeId(targetTmpPost.getPostType().getId())
                    .tagIds(targetTmpPost.getTmpPostTags().stream().map(tmpPostTag -> tmpPostTag.getTag().getId()).toList())
                    .isEnabled(true)
                    .build();


            // when
            tmpPostService.convertToPost(validId);

            // then
            //결과 값은 검사하지 않음. 왜냐하면 어쩌피 결과값은 Mocking 된 것이기 때문에 의미가 없음.
            //즉 create 메소드에 정상적인 값이 들어갔는지만 확인하면 됨.
            verify(tmpPostRepository).findPostById(eq(validId));
            verify(tmpPostRepository).delete(eq(targetTmpPost));
            verify(postService).create(eq(validRequest));
        }

        private static Stream<Arguments> provideInvalidTmpPost() {
            var baseTmpPost = createTmpPost("1");

            // 시리즈가 필요한 게시글인데 시리즈가 없는 경우
            var noSeriesTmpPost = copyTmpPost(baseTmpPost);
            noSeriesTmpPost.setSeries(null);
            noSeriesTmpPost.setPostType(createSeriesPostType(true));

            // 시리즈가 필요 없는 게시글인데 시리즈가 있는 경우
            var hasSeriesTmpPost = copyTmpPost(baseTmpPost);
            hasSeriesTmpPost.setSeries(SeriesTestDataFactory.createSeries(1L));
            hasSeriesTmpPost.setPostType(createSeriesPostType(false));

            // 타이틀이 없는 경우
            var noTitleTmpPost = copyTmpPost(baseTmpPost);
            noTitleTmpPost.setTitle("");


            // 타이틀이 3자 미만인 경우
            var shortTitleTmpPost = copyTmpPost(baseTmpPost);
            shortTitleTmpPost.setTitle("12");

            // 컨텐츠가 없는 경우
            var noContentTmpPost = copyTmpPost(baseTmpPost);
            noContentTmpPost.setContent("");

            // 컨텐츠가 3자 미만인 경우
            var shortContentTmpPost = copyTmpPost(baseTmpPost);
            shortContentTmpPost.setContent("12");

            return Stream.of(
                    Arguments.of(noSeriesTmpPost, "시리즈가 필요한 게시글인데 시리즈가 없는 경우"),
                    Arguments.of(hasSeriesTmpPost, "시리즈가 필요 없는 게시글인데 시리즈가 있는 경우"),
                    Arguments.of(noTitleTmpPost, "타이틀이 없는 경우"),
                    Arguments.of(shortTitleTmpPost, "타이틀이 3자 미만인 경우"),
                    Arguments.of(noContentTmpPost, "컨텐츠가 없는 경우"),
                    Arguments.of(shortContentTmpPost, "컨텐츠가 3자 미만인 경우")
            );
        }

        @DisplayName("올바르지 않은 임시 포스트 ID를 입력하면 BusinessException을 던진다.")
        @MethodSource("provideInvalidTmpPost")
        @ParameterizedTest(name = "{index} : {1} 인 경우")
        void convertToPost_fail(TmpPost invalidTmpPost, String message) {
            // given
            doReturn(Optional.of(invalidTmpPost)).when(tmpPostRepository).findPostById(eq(invalidTmpPost.getId()));

            // when
            var exception = assertThrows(BusinessException.class, () -> tmpPostService.convertToPost(invalidTmpPost.getId()));

            // then
            assertEquals(BusinessErrorCode.POLICY_VIOLATE, exception.getErrorCode());
            verify(tmpPostRepository).findPostById(eq(invalidTmpPost.getId()));
            verifyNoInteractions(postService);
        }


    }


}
