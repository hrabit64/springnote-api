package com.springnote.api.domain.post.custom;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.MultiValueMap;

import com.springnote.api.domain.post.Post;

public interface PostQRepository {

    Page<Post> matchByTitle(String title, MultiValueMap<String, String> searchOptions, Pageable pageable);
    Page<Post> matchByContent(String content, MultiValueMap<String, String> searchOptions, Pageable pageable);
    Page<Post> matchByTitleOrContent(String keyword, MultiValueMap<String, String> searchOptions, Pageable pageable);

}
