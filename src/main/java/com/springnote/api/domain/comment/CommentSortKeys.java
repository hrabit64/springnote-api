package com.springnote.api.domain.comment;

import com.springnote.api.domain.SortKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Comment 엔티티에서 사용가능한 정렬 키를 정의합니다.
 */
@Getter
@RequiredArgsConstructor
public enum CommentSortKeys implements SortKeys {
    ID("id"),
    USER("user"),
    CREATED_DATE("createdDate"),
    LAST_MODIFIED_DATE("lastModifiedDate");


    private final String name;

}
