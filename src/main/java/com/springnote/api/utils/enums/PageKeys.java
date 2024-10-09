package com.springnote.api.utils.enums;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

@Getter
@RequiredArgsConstructor
public enum PageKeys {
    PAGE("page"),
    SIZE("size"),
    SORT("sort"),
    DIRECTION("direction");

    private final String key;

    public static List<PageKeys> getAllKeys() {
        return Arrays.asList(PageKeys.values());
    }

}
