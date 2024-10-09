package com.springnote.api.testUtils.dataFactory.tag;

import com.springnote.api.dto.tag.common.TagResponseDto;

public class TagDtoTestDataFactory {
    public static TagResponseDto createTagResponseDto() {
        return TagResponseDto.builder()
                .id(1L)
                .name("tag")
                .build();
    }
}
