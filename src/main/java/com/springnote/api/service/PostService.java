package com.springnote.api.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import com.springnote.api.domain.post.PostRepository;
import com.springnote.api.domain.postEditorContent.PostEditorContentRepository;
import com.springnote.api.domain.postTag.PostTagRepository;
import com.springnote.api.domain.series.SeriesRepository;
import com.springnote.api.domain.tag.TagRepository;
import com.springnote.api.dto.post.common.PostDetailResponseCommonDto;
import com.springnote.api.dto.post.common.PostSimpleResponseCommonDto;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.markdown.MarkdownHelper;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostEditorContentRepository postEditorContentRepository;
    private final PostTagRepository postTagRepository;
    private final TagRepository tagRepository;
    private final SeriesRepository seriesRepository;
    private final MarkdownHelper markdownHelper;
    
    @Transactional(readOnly = true)
    public PostDetailResponseCommonDto get(Long id) {
        return postRepository.findById(id).map(PostDetailResponseCommonDto::new)
                .orElseThrow(() -> new BusinessException(
                        "( " + id + " ) 에 해당하는 게시글이 없습니다.", BusinessErrorCode.ITEM_NOT_FOUND));
    }

    @Transactional(readOnly = true)
    public Page<PostSimpleResponseCommonDto> get(String keyword, MultiValueMap<String, String> searchOptions,
            Pageable pageable, SearchMode searchMode) {

        return switch (searchMode) {
            case TITLE ->
                postRepository.matchByTitle(keyword, searchOptions, pageable).map(PostSimpleResponseCommonDto::new);
            case CONTENT ->
                postRepository.matchByContent(keyword, searchOptions, pageable).map(PostSimpleResponseCommonDto::new);
            case MIX -> postRepository.matchByTitleOrContent(keyword, searchOptions, pageable)
                    .map(PostSimpleResponseCommonDto::new);
            default -> throw new IllegalArgumentException("( " + searchMode + " ) 에 해당하는 검색 옵션이 존재하지 않습니다. ");
        };

    }

    @Transactional
    public void create() {

    }

    @Transactional
    public void update() {

    }

    @Transactional
    public void delete() {

    }

    public static enum SearchMode {
        TITLE, CONTENT, MIX
    }
}
