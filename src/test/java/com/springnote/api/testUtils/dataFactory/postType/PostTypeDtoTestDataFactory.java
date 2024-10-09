package com.springnote.api.testUtils.dataFactory.postType;

import com.springnote.api.dto.postType.common.PostTypeResponseDto;

public class PostTypeDtoTestDataFactory {

    public static PostTypeResponseDto createPostTypeResponseDto() {
        return PostTypeResponseDto.builder()
                .id(1L)
                .name("name")
                .build();
    }
}
