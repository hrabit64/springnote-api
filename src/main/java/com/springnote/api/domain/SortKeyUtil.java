package com.springnote.api.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class SortKeyUtil {

    public static <T extends Enum<T> & SortKeys> List<T> getAllKeys(Class<T> enumClass) {
        return Arrays.asList(enumClass.getEnumConstants());
    }


    public static <T extends Enum<T> & SortKeys> T getKey(Class<T> enumClass, String name) {
        return Stream.of(enumClass.getEnumConstants())
                .filter(sortKeys -> sortKeys.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
