package com.springnote.api.service;

import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.postType.PostTypeRepository;
import com.springnote.api.domain.series.Series;
import com.springnote.api.domain.series.SeriesRepository;
import com.springnote.api.domain.tag.Tag;
import com.springnote.api.domain.tag.TagRepository;
import com.springnote.api.domain.tmpPost.TmpPost;
import com.springnote.api.domain.tmpPost.TmpPostRepository;
import com.springnote.api.domain.tmpPostTag.TmpPostTag;
import com.springnote.api.domain.tmpPostTag.TmpPostTagRepository;
import com.springnote.api.dto.post.common.PostDetailResponseCommonDto;
import com.springnote.api.dto.post.service.PostCreateRequestServiceDto;
import com.springnote.api.dto.tmpPost.common.TmpPostResponseCommonDto;
import com.springnote.api.dto.tmpPost.service.TmpPostCreateRequestServiceDto;
import com.springnote.api.dto.tmpPost.service.TmpPostUpdateRequestServiceDto;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import com.springnote.api.utils.tag.TagComparator;
import com.springnote.api.utils.tag.TagComparisonResult;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class TmpPostService {

    private final TmpPostRepository tmpPostRepository;
    private final TmpPostTagRepository tmpPostTagRepository;
    private final TagRepository tagRepository;
    private final SeriesRepository seriesRepository;
    private final PostTypeRepository postTypeRepository;
    private final TagComparator<TmpPostTag> tagComparator;
    private final PostService postService;

    @Transactional(readOnly = true)
    public TmpPostResponseCommonDto getById(String id) {
        var target = fetchTmpPostById(id);

        return new TmpPostResponseCommonDto(target);
    }

    private TmpPost fetchTmpPostById(String id) {
        return tmpPostRepository.findPostById(id).orElseThrow(
                () -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(id,
                                "임시 게시글"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<TmpPostResponseCommonDto> getAll(Pageable pageable) {
        return tmpPostRepository.findAllBy(pageable).map(TmpPostResponseCommonDto::new);
    }

    @Transactional
    public TmpPostResponseCommonDto create(TmpPostCreateRequestServiceDto requestDto) {

        var targetPostType = fetchPostTypeById(requestDto);
        // 추후 게시글 생성시 검증에서만 통과하면 문제 없음.

//
//        if (isViolateNeedSeriesPolicy(targetPostType, requestDto.getSeriesId())) {
//            throw new BusinessException(
//                    "해당 게시글 타입의 시리즈 정책에 맞지 않는 요청입니다. 해당 게시글 타입의 시리즈 정책은 ("
//                            + ((targetPostType.isNeedSeries()) ? "시리즈 필수" : "시리즈 불필요") + ") 입니다.",
//                    BusinessErrorCode.POLICY_VIOLATE);
//        }

        // 시리즈 검증
        var targetSeries = (requestDto.getSeriesId() != null)
                ? fetchSeriesById(requestDto)
                : null;

        // 태그 검증
        List<Tag> tags = getTagsWithValidation(requestDto);

        var tmpPost = requestDto.toEntity(targetSeries, targetPostType);

        var savedTmpPost = tmpPostRepository.save(tmpPost);

        // 태그 저장
        createTmpPostTag(requestDto, tags, savedTmpPost);

        return new TmpPostResponseCommonDto(savedTmpPost);
    }

    private void createTmpPostTag(TmpPostCreateRequestServiceDto requestDto, List<Tag> tags, TmpPost tmpPost) {
        if (!requestDto.getTagIds().isEmpty()) {
            var postTags = tags.stream().map(tag -> tag.toTmpPostTag(tmpPost)).toList();
            var savedPostTag = tmpPostTagRepository.saveAll(postTags);
            tmpPost.setTmpPostTags(savedPostTag);
        }
    }

    private @NotNull List<Tag> getTagsWithValidation(TmpPostCreateRequestServiceDto requestDto) {
        List<Tag> tags = List.of();
        if (!requestDto.getTagIds().isEmpty()) {
            tags = tagRepository
                    .findTagsByIdIn(requestDto.getTagIds().stream().distinct().collect(Collectors.toList()));

            if (!isExistAllTag(requestDto.getTagIds(), tags)) {
                throw new BusinessException("게시글에 찾을 수 없는 태그가 포함되어있습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
            }

        }
        return tags;
    }

    private Series fetchSeriesById(TmpPostCreateRequestServiceDto requestDto) {
        return seriesRepository.findById(requestDto.getSeriesId())
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(requestDto.getSeriesId().toString(),
                                "시리즈"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    private PostType fetchPostTypeById(TmpPostCreateRequestServiceDto requestDto) {
        return postTypeRepository.findById(requestDto.getPostTypeId())
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(requestDto.getPostTypeId().toString(),
                                "게시글 유형"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    @Transactional
    public TmpPostResponseCommonDto update(TmpPostUpdateRequestServiceDto requestDto) {
        var targetTmpPost = fetchTmpPostById(requestDto.getId());

        // 임시 포스트는 검증하지 않음.
        // 추후 게시글 생성시 검증에서만 통과하면 문제 없음.
//        if (isViolateNeedSeriesPolicy(targetTmpPost.getPostType(), requestDto.getSeriesId())) {
//            throw new BusinessException(
//                    "해당 게시글 타입의 시리즈 정책에 맞지 않는 요청입니다. 해당 게시글 타입의 시리즈 정책은 ("
//                            + ((targetTmpPost.getPostType().isNeedSeries()) ? "시리즈 필수" : "시리즈 불필요") + ") 입니다.",
//                    BusinessErrorCode.POLICY_VIOLATE);
//        }

        updateSeries(targetTmpPost, requestDto.getSeriesId());
        updatePostTag(targetTmpPost, requestDto.getTagIds());
        targetTmpPost.update(requestDto.toEntity());

        var updatedTmpPost = tmpPostRepository.save(targetTmpPost);

        return new TmpPostResponseCommonDto(updatedTmpPost);

    }

    @Transactional
    public TmpPostResponseCommonDto delete(String id) {
        var targetTmpPost = fetchTmpPostById(id);

        tmpPostRepository.delete(targetTmpPost);

        return new TmpPostResponseCommonDto(targetTmpPost);
    }

    @Transactional
    public PostDetailResponseCommonDto convertToPost(String id) {
        var targetTmpPost = fetchTmpPostById(id);

        verifyTmpPost(targetTmpPost);

        var request = PostCreateRequestServiceDto.builder()
                .content(targetTmpPost.getContent())
                .title(targetTmpPost.getTitle())
                .thumbnail(targetTmpPost.getThumbnail())
                .seriesId(targetTmpPost.getSeries().getId())
                .tagIds(targetTmpPost.getTmpPostTags().stream().map(tmpPostTag -> tmpPostTag.getTag().getId()).toList())
                .postTypeId(targetTmpPost.getPostType().getId())
                .isEnabled(true)
                .build();

        var result = postService.create(request);

        tmpPostRepository.delete(targetTmpPost);

        return result;

    }

    private void verifyTmpPost(TmpPost tmpPost) {
        if (tmpPost.getTitle() == null || tmpPost.getTitle().isEmpty()) {
            throw new BusinessException(ExceptionMessageFormatter.createFailedVerifyMessage("제목", "필수 값"),
                    BusinessErrorCode.POLICY_VIOLATE);
        }

        // 넘는건 이미 컨트롤러에서 검증함.
        if (tmpPost.getTitle().length() < 3) {
            throw new BusinessException(ExceptionMessageFormatter.createFailedVerifyMessage("제목", "3자 이상 이어야합니다."),
                    BusinessErrorCode.POLICY_VIOLATE);
        }


        if (tmpPost.getContent() == null || tmpPost.getContent().isEmpty()) {
            throw new BusinessException(ExceptionMessageFormatter.createFailedVerifyMessage("본문", "필수 값"),
                    BusinessErrorCode.POLICY_VIOLATE);
        }

        // 넘는건 이미 컨트롤러에서 검증함.
        if (tmpPost.getContent().length() < 3) {
            throw new BusinessException(ExceptionMessageFormatter.createFailedVerifyMessage("본문", "3자 이상 이어야합니다."),
                    BusinessErrorCode.POLICY_VIOLATE);
        }

        if (tmpPost.getPostType().isNeedSeries() && tmpPost.getSeries() == null) {
            throw new BusinessException(ExceptionMessageFormatter.createFailedVerifyMessage("시리즈", "시리즈가 필요한 게시글 유형"),
                    BusinessErrorCode.POLICY_VIOLATE);
        }

        if (!tmpPost.getPostType().isNeedSeries() && tmpPost.getSeries() != null) {
            throw new BusinessException(ExceptionMessageFormatter.createFailedVerifyMessage("시리즈", "시리즈가 필요없는 게시글 유형"),
                    BusinessErrorCode.POLICY_VIOLATE);
        }

    }

    private boolean isViolateNeedSeriesPolicy(PostType postType, Long seriesId) {
        return (postType.isNeedSeries() && seriesId == null) || (!postType.isNeedSeries() && seriesId != null);
    }

    private boolean isExistAllTag(List<Long> tagIds, List<Tag> tags) {
        return tagIds.size() == tags.size();
    }

    private void updateSeries(TmpPost targetPost, Long newSeriesId) {
        if (isNeedUpdateSeries(targetPost, newSeriesId)) {
            var newSeries = seriesRepository.findById(newSeriesId).orElseThrow(
                    () -> new BusinessException(
                            ExceptionMessageFormatter.createItemNotFoundMessage(newSeriesId.toString(), "시리즈"),
                            BusinessErrorCode.ITEM_NOT_FOUND));

            targetPost.setSeries(newSeries);
        }
    }

    private void updatePostTag(TmpPost targetPost, List<Long> newTagIds) {
        var result = tagComparator.getTagCompareResult(targetPost.getTmpPostTags(), newTagIds);

        if (result.isChanged()) {

            if (!result.getRemovedTag().isEmpty()) {
                deleteTag(targetPost, result);
            }

            // 새로추가를 원하는 태그 정보를 추가한다.

            if (!result.getAddedTag().isEmpty()) {
                createTag(targetPost, result);
            }
        }

    }

    private void createTag(TmpPost targetPost, TagComparisonResult<TmpPostTag> result) {
        var tags = tagRepository.findTagsByIdIn(result.getAddedTag());

        if (!isExistAllTag(result.getAddedTag(), tags)) {
            throw new BusinessException("게시글에 찾을 수 없는 태그가 포함되어있습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        var postTags = tags.stream().map(tag -> tag.toTmpPostTag(targetPost)).toList();
        var savedPostTags = tmpPostTagRepository.saveAll(postTags);
        targetPost.addTmpPostTag(savedPostTags);
    }

    private void deleteTag(TmpPost targetPost, TagComparisonResult<TmpPostTag> result) {
        tmpPostTagRepository.deleteAllById(result.getRemovedTag().stream().map(TmpPostTag::getId).toList());
        targetPost.deleteTmpPostTag(result.getRemovedTag());
    }

    private boolean isNeedUpdateSeries(TmpPost tmpPost, Long newId) {
        if (!tmpPost.getPostType().isNeedSeries()) return false;
        return !tmpPost.getSeries().getId().equals(newId);
    }


}
