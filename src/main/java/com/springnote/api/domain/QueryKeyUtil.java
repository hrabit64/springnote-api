package com.springnote.api.domain;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class QueryKeyUtil {
    //get All keys
    public static <T extends Enum<T> & QueryKeys> List<T> getAllKeys(Class<T> enumClass) {
        return Arrays.asList(enumClass.getEnumConstants());
    }

    /**
     * 키 이름으로 주어진 QueryKeys의 Key를 찾아 반환한다.
     *
     * @param enumClass
     * @param name      키 이름(대소문자 무시)
     * @param <T>
     * @return
     */
    public static <T extends Enum<T> & QueryKeys> T getKey(Class<T> enumClass, String name) {
        return Stream.of(enumClass.getEnumConstants())
                .filter(queryKeys -> queryKeys.getQueryString().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
