package com.springnote.api.domain.tag;

import com.springnote.api.domain.QueryKeys;
import com.springnote.api.utils.validation.query.comm.QueryKeyCommOption;
import com.springnote.api.utils.validation.query.ind.QueryKeyIndOption;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;


@RequiredArgsConstructor
@Getter
public enum TagQueryKeys implements QueryKeys {
    NAME("name", Long.class);

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