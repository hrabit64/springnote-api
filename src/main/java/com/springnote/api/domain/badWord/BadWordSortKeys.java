package com.springnote.api.domain.badWord;

import com.springnote.api.domain.SortKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


/**
 * BadWord 엔티티에서 사용가능한 정렬 키를 정의합니다.
 */
@RequiredArgsConstructor
@Getter
public enum BadWordSortKeys implements SortKeys {
    ID("id"),
    WORD("word"),
    TYPE("type");

    private final String name;


    @Override
    public String getName() {
        return name;
    }
}
