package com.springnote.api.domain.image;

import com.springnote.api.domain.SortKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ImageSortKeys implements SortKeys {
    ID("id"),
    POST_ID("postId"),
    CREATED_AT("createdAt");

    private final String name;
}
