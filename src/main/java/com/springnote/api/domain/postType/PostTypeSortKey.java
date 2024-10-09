package com.springnote.api.domain.postType;

import com.springnote.api.domain.SortKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum PostTypeSortKey implements SortKeys {
    ID("id"),
    NAME("name");

    private final String name;

}
