package com.springnote.api.domain.badWord;

import com.springnote.api.domain.QueryKeys;
import com.springnote.api.utils.validation.query.comm.QueryKeyCommOption;
import com.springnote.api.utils.validation.query.ind.QueryKeyIndOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * BadWord 엔티티에서 사용가능한 쿼리 키를 정의합니다.
 */
@RequiredArgsConstructor
@Getter
public enum BadWordQueryKeys implements QueryKeys {
    WORD("word", String.class, null, null),
    TYPE("type", Boolean.class, null, null);

    private final String queryString;
    private final Class<?> type;
    private final List<QueryKeyIndOption> IndOptions;
    private final List<QueryKeyCommOption> commOptions;

    public List<List<String>> getRequiredFields() {
        return List.of();
    }
}
