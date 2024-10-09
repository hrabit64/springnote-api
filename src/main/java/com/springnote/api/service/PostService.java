package com.springnote.api.service;

import com.springnote.api.domain.content.Content;
import com.springnote.api.domain.content.ContentRepository;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.post.PostQueryKeys;
import com.springnote.api.domain.post.PostRepository;
import com.springnote.api.domain.postTag.PostTag;
import com.springnote.api.domain.postTag.PostTagRepository;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.postType.PostTypeRepository;
import com.springnote.api.domain.series.Series;
import com.springnote.api.domain.series.SeriesRepository;
import com.springnote.api.domain.tag.Tag;
import com.springnote.api.domain.tag.TagRepository;
import com.springnote.api.dto.post.common.PostDetailResponseCommonDto;
import com.springnote.api.dto.post.common.PostSimpleResponseCommonDto;
import com.springnote.api.dto.post.service.PostCreateRequestServiceDto;
import com.springnote.api.dto.post.service.PostStatusUpdateRequestServiceDto;
import com.springnote.api.dto.post.service.PostUpdateRequestServiceDto;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import com.springnote.api.utils.markdown.MarkdownHelper;
import com.springnote.api.utils.tag.TagComparator;
import com.springnote.api.utils.tag.TagComparisonResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class PostService {


    private final PostRepository postRepository;
    private final ContentRepository contentRepository;
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;
    private final SeriesRepository seriesRepository;
    private final MarkdownHelper markdownHelper;
    private final PostTypeRepository postTypeRepository;
    private final TagComparator<PostTag> tagComparator;

    /**
     * 게시글 상세 정보를 조회합니다.
     *
     * @param id 조회할 게시글의 식별자
     * @throws BusinessException 조회할 게시글이 존재하지 않을 경우
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    @Transactional(readOnly = true)
    public PostDetailResponseCommonDto getById(Long id) {
        var post = fetchPostById(id);
        return new PostDetailResponseCommonDto(post);
    }


    @Transactional(readOnly = true)
    public Page<PostSimpleResponseCommonDto> getAllByTitleKeyword(String keyword, MultiValueMap<PostQueryKeys, String> searchOptions, Pageable pageable) {

        return postRepository.matchByTitle(keyword, searchOptions, pageable).map(PostSimpleResponseCommonDto::new);

    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponseCommonDto> getAllByContentKeyword(String keyword, MultiValueMap<PostQueryKeys, String> searchOptions, Pageable pageable) {

        return postRepository.matchByContent(keyword, searchOptions, pageable).map(PostSimpleResponseCommonDto::new);

    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponseCommonDto> getAllByMixKeyword(String keyword, MultiValueMap<PostQueryKeys, String> searchOptions, Pageable pageable) {

        return postRepository.matchByMix(keyword, searchOptions, pageable).map(PostSimpleResponseCommonDto::new);

    }


    @Transactional(readOnly = true)
    public Page<PostSimpleResponseCommonDto> getAllByQueryParams(MultiValueMap<PostQueryKeys, String> QueryParams,
                                                                 Pageable pageable) {

        return postRepository.findAllPostWithQueryParam(QueryParams, pageable).map(PostSimpleResponseCommonDto::new);
    }

//    @Transactional(readOnly = true)
//    public Page<PostSimpleResponseCommonDto> getAll(Pageable pageable) {
//
//        return postRepository.findAllPost(pageable).map(PostSimpleResponseCommonDto::new);
//    }

    /**
     * 게시글을 생성합니다.
     */
    @Transactional
    public PostDetailResponseCommonDto create(PostCreateRequestServiceDto requestDto) {

        validateTitleDoesNotExist(requestDto.getTitle());

        var targetPostType = getPostTypeById(requestDto.getPostTypeId());

        validatePostTypePolicy(targetPostType, requestDto.getSeriesId());

        var targetSeries = (requestDto.getSeriesId() != null)
                ? getSeriesById(requestDto.getSeriesId())
                : null;

        //본문 생성
        var plainText = markdownHelper.toPlainText(requestDto.getContent());
        var postContent = Content.builder()
                .plainText(plainText)
                .editorText(requestDto.getContent())
                .build();

        var savedPostContent = contentRepository.save(postContent);

        // 포스트 생성
        var post = requestDto.toEntity(targetSeries, targetPostType, savedPostContent);
        var savedPost = postRepository.save(post);

        createPostTag(requestDto.getTagIds(), savedPost);

        return new PostDetailResponseCommonDto(savedPost);
    }

    @Transactional
    public PostDetailResponseCommonDto update(PostUpdateRequestServiceDto requestDto) {

        var targetPost = fetchPostById(requestDto.getId());

        validatePostTypePolicy(targetPost.getPostType(), requestDto.getSeriesId());
        updatePostTitle(targetPost, requestDto.getTitle());
        updateContent(targetPost, requestDto.getContent());
        updatePostTag(targetPost, requestDto.getTagIds());
        updateSeries(targetPost, requestDto.getSeriesId());

        targetPost.setEnabled(requestDto.isEnabled());
        targetPost.setThumbnail(requestDto.getThumbnail());

        var updatedPost = postRepository.save(targetPost);

        return new PostDetailResponseCommonDto(updatedPost);
    }

    /**
     * 게시글의 상태를 변경합니다.
     *
     * @param requestDto 변경하려는 게시글의 식별자와 변경하려는 상태
     * @return 변경된 게시글의 상세 정보
     * @throws BusinessException 변경하려는 게시글이 존재하지 않을 경우
     * @throws BusinessException 변경하려는 상태가 현재 상태와 동일할 경우
     */
    @Transactional
    public PostDetailResponseCommonDto updateStatus(PostStatusUpdateRequestServiceDto requestDto) {

        var targetPost = fetchPostById(requestDto.getId());

        validateNewStatus(requestDto, targetPost);

        targetPost.setEnabled(requestDto.isEnabled());

        var updatedPost = postRepository.save(targetPost);

        return new PostDetailResponseCommonDto(updatedPost);
    }

    @Transactional
    public PostDetailResponseCommonDto delete(Long id) {
        var targetPost = fetchPostById(id);

        contentRepository.delete(targetPost.getContent());
        postRepository.delete(targetPost);

        return new PostDetailResponseCommonDto(targetPost);
    }

    @Transactional
    public boolean isExistTitle(String title) {
        return postRepository.existsByTitle(title);
    }

    private Series getSeriesById(Long id) {
        return seriesRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(id.toString(),
                                "시리즈"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    private PostType getPostTypeById(Long id) {
        return postTypeRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(id.toString(),
                                "게시글 유형"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    private Post fetchPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(id.toString(), "게시글"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    private void validateTitleDoesNotExist(String title) {
        if (postRepository.existsByTitle(title)) {
            throw new BusinessException(ExceptionMessageFormatter.createItemAlreadyExistMessage(title, "게시글"),
                    BusinessErrorCode.ITEM_ALREADY_EXIST);
        }
    }

    private void validatePostTypePolicy(PostType postType, Long seriesId) {

        // 시리즈 정책 검증
        if ((postType.isNeedSeries() && seriesId == null) || (!postType.isNeedSeries() && seriesId != null)) {
            throw new BusinessException(
                    "해당 게시글 타입의 시리즈 정책에 맞지 않는 요청입니다. 해당 게시글 타입의 시리즈 정책은 ("
                            + ((postType.isNeedSeries()) ? "시리즈 필수" : "시리즈 불필요") + ") 입니다.",
                    BusinessErrorCode.POLICY_VIOLATE);
        }
    }


    private void validateTag(List<Long> tagIds, List<Tag> tags) {
        if (tagIds.size() != tags.size()) {
            throw new BusinessException("게시글에 찾을 수 없는 태그가 포함되어있습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }
    }

    private void createPostTag(List<Long> tagIds, Post post) {
        var tags = tagRepository.findTagsByIdIn(tagIds);

        validateTag(tagIds, tags);

        var postTags = tags.stream().map(tag -> tag.toPostTag(post)).collect(Collectors.toList());
        var savedPostTag = postTagRepository.saveAll(postTags);

        post.addPostTags(savedPostTag);
    }

    private void updatePostTitle(Post targetPost, String newTitle) {
        if (isTitleChange(targetPost, newTitle)) {
            validateTitleDoesNotExist(newTitle);

            targetPost.setTitle(newTitle);
        }
    }

    private static boolean isTitleChange(Post targetPost, String newTitle) {
        return !targetPost.getTitle().equals(newTitle);
    }

    private void updateContent(Post targetPost, String newContent) {
        var targetPostContent = targetPost.getContent();

        if (isContentChange(newContent, targetPostContent)) {
            targetPostContent.setEditorText(newContent);
            targetPostContent.setPlainText(markdownHelper.toPlainText(newContent));
            contentRepository.save(targetPostContent);
            targetPost.setContent(targetPostContent);
        }
    }

    //TODO 긴 글 수정시에 이렇게 비교하면 병목이 발생할 수 있음. 추후에 수정 필요
    private static boolean isContentChange(String newContent, Content targetPostContent) {
        return !targetPostContent.getEditorText().equals(newContent);
    }

    private static void validateNewStatus(PostStatusUpdateRequestServiceDto requestDto, Post targetPost) {
        if (targetPost.isEnabled() == requestDto.isEnabled()) {
            throw new BusinessException("변경하려는 상태가 현재 상태와 동일합니다.", BusinessErrorCode.ITEM_CONFLICT);
        }
    }

    private void updateSeries(Post targetPost, Long newSeriesId) {
        if (isNeedUpdateSeries(targetPost.getPostType().isNeedSeries(), targetPost.getSeries(), newSeriesId)) {
            var newSeries = getSeriesById(newSeriesId);
            targetPost.setSeries(newSeries);
        }
    }

    private void updatePostTag(Post targetPost, List<Long> newTagIds) {
        var result = tagComparator.getTagCompareResult(targetPost.getPostTags(), newTagIds);

        if (result.isChanged()) {

            if (!result.getRemovedTag().isEmpty()) {
                deletePostTag(targetPost, result);
            }

            //새로 추가된 태그가 있을 경우
            if (!result.getAddedTag().isEmpty()) {
                createPostTag(result.getAddedTag(), targetPost);
            }

        }

    }

    private void deletePostTag(Post targetPost, TagComparisonResult<PostTag> result) {
        postTagRepository.deleteAllById(result.getRemovedTag().stream().map(PostTag::getId).toList());
        targetPost.deletePostTag(result.getRemovedTag());
    }

    private boolean isNeedUpdateSeries(boolean isNeedSeries, Series series, Long newId) {
        if (!isNeedSeries) return false;

        //왜냐하면 이미 앞에서 정책을 검사했기 때문에 null이 아님을 보장할 수 있음
        if (series == null) throw new IllegalArgumentException("포스트 타입 정책 검사에 문제가 있습니다.");

        return !Objects.equals(series.getId(), newId);
    }
}
