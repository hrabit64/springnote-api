package com.springnote.api.domain.post;

import com.springnote.api.domain.SortKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * 포스트 엔티티에서 사용 가능한 정렬 키
 */
@RequiredArgsConstructor
@Getter
public enum PostSortKeys implements SortKeys {
    ID("id"),
    TITLE("title"),
    CONTENT("content"),
    IS_OPEN("isOpen"),
    SERIES("series"),
    LAST_MODIFIED_DATE("lastModifiedDate"),
    CREATED_DATE("createdDate");

    private final String name;


}
