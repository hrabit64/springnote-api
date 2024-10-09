package com.springnote.api.domain.post;

import com.springnote.api.domain.QueryKeys;
import com.springnote.api.utils.validation.query.comm.LimitFieldCount;
import com.springnote.api.utils.validation.query.comm.QueryKeyCommOption;
import com.springnote.api.utils.validation.query.comm.UniqueItems;
import com.springnote.api.utils.validation.query.ind.NumberRange;
import com.springnote.api.utils.validation.query.ind.QueryKeyIndOption;
import com.springnote.api.utils.validation.query.ind.StringLength;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


/**
 * 포스트 엔티티에서 사용가능한 쿼리 키
 */
@RequiredArgsConstructor
@Getter
public enum PostQueryKeys implements QueryKeys {

    POST_TYPE("postType",
            Long.class,
            List.of(NumberRange.of(Long.class)),
            null
    ),

    SERIES("series",
            Long.class,
            List.of(NumberRange.of(Long.class)),
            null
    ),

    TAG("tag",
            Long.class,
            List.of(NumberRange.of(Long.class)),
            List.of(new LimitFieldCount(null, 10), new UniqueItems())
    ),

    IS_ONLY_OPEN_POST(
            "isOnlyOpenPost",
            Boolean.class,
            null,
            List.of(new LimitFieldCount(1, 1))
    ),

    KEYWORD("keyword",
            String.class,
            List.of(new StringLength(2, 20)),
            null
    ),

    SEARCH_MODE("searchMode",
            PostKeywordSearchMode.class,
            null,
            List.of(new LimitFieldCount(1, 1))
    );

    private final String queryString;
    private final Class<?> type;
    private final List<QueryKeyIndOption> IndOptions;
    private final List<QueryKeyCommOption> commOptions;

    public List<List<String>> getRequiredFields() {
        return List.of(
                List.of(KEYWORD.getQueryString(), SEARCH_MODE.getQueryString())
        );
    }

}