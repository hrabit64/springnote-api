package com.springnote.api.domain.tmpPost;

import com.springnote.api.domain.QueryKeys;
import com.springnote.api.utils.validation.query.comm.QueryKeyCommOption;
import com.springnote.api.utils.validation.query.ind.QueryKeyIndOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
@Getter
public enum TmpPostQueryKeys implements QueryKeys {
    POST_TYPE("postType", Long.class),
    SERIES("series", Long.class),
    TAG("tag", Long.class);

    private final String queryString;
    private final Class<?> type;

    @Override
    public List<QueryKeyIndOption> getIndOptions() {
        return List.of();
    }

    @Override
    public List<QueryKeyCommOption> getCommOptions() {
        return List.of();
    }

    @Override
    public List<List<String>> getRequiredFields() {
        return List.of();
    }
}