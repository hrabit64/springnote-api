package com.springnote.api.testUtils.dataFactory.tag;

import com.springnote.api.domain.tag.Tag;
import com.springnote.api.testUtils.RandomStringGenerator;

public class TagTestDataFactory {
    public static Tag createTag() {
        return Tag.builder()
                .id(1L)
                .name(RandomStringGenerator.generateRandomString(5))
                .build();
    }

    public static Tag createTag(Long id) {
        return Tag.builder()
                .id(id)
                .name(RandomStringGenerator.generateRandomString(5))
                .build();
    }

    public static Tag createTag(Long id, String name) {
        return Tag.builder()
                .id(id)
                .name(name)
                .build();
    }

    public static Tag createTag(String name) {
        return Tag.builder()
                .id(1L)
                .name(name)
                .build();
    }
}
