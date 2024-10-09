package com.springnote.api.domain.series;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import com.springnote.api.domain.SortKeys;
import lombok.Getter;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Getter
public enum SeriesSortKeys implements SortKeys {
    ID("id"),
    NAME("name");

    private final String name;
}