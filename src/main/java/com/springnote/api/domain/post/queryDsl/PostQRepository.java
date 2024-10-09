package com.springnote.api.domain.post.queryDsl;

import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.post.PostQueryKeys;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

public interface PostQRepository {

    /**
     * 제목으로 포스트를 검색합니다.
     *
     * @param title         제목
     * @param searchOptions 쿼리 옵션
     * @param pageable
     * @return
     */
    Page<Post> matchByTitle(String title, MultiValueMap<PostQueryKeys, String> searchOptions, Pageable pageable);

    /**
     * 내용으로 포스트를 검색합니다.
     *
     * @param content       내용
     * @param searchOptions 쿼리 옵션
     * @param pageable
     * @return
     */
    Page<Post> matchByContent(String content, MultiValueMap<PostQueryKeys, String> searchOptions, Pageable pageable);

    /**
     * 제목과 내용으로 포스트를 검색합니다.
     *
     * @param keyword       검색어
     * @param searchOptions 쿼리 옵션
     * @param pageable
     * @return
     */
    Page<Post> matchByMix(String keyword, MultiValueMap<PostQueryKeys, String> searchOptions, Pageable pageable);

    /**
     * 포스트를 전체 조회합니다.
     *
     * @param pageable
     * @return
     */
    Page<Post> findAllPost(Pageable pageable);

    /**
     * 쿼리 옵션을 이용하여 포스트를 조회합니다.
     *
     * @param searchOptions 쿼리 옵션
     * @param pageable
     * @return
     */
    Page<Post> findAllPostWithQueryParam(MultiValueMap<PostQueryKeys, String> searchOptions, Pageable pageable);
}
