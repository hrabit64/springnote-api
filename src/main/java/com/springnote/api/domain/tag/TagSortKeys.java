package com.springnote.api.domain.tag;

import com.springnote.api.domain.SortKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TagSortKeys implements SortKeys {
    ID("id"),
    NAME("name");
//    LAST_MODIFIED_DATE("lastModifiedDate"),
//    CREATED_DATE("createdDate");

    private final String name;


}
