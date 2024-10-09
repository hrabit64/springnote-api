package com.springnote.api.domain.tmpPost;

import com.springnote.api.domain.SortKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum TmpPostSortKeys implements SortKeys {

    LAST_MODIFIED_DATE("lastModifiedDate"),
    CREATED_DATE("createdDate");

    private final String name;


}
