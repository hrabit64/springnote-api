package com.springnote.api.utils.testDataBuilder;

import com.springnote.api.domain.tag.Tag;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestTagBuilder {
    private Long id;

    @Builder.Default
    private String name = "자동생성된태그";

    public Tag toEntity() {
        return Tag.builder()
                .id(id)
                .name(name)
                .build();
    }
}
