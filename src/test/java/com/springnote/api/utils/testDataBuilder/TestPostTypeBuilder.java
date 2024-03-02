package com.springnote.api.utils.testDataBuilder;


import com.springnote.api.domain.postType.PostType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestPostTypeBuilder {
    private Long id;

    @Builder.Default
    private String name = "자동생성된타입";

    @Builder.Default
    private boolean isCanAddComment = true;
    
    @Builder.Default
    private boolean isCanAddLike = true;
    
    @Builder.Default
    private boolean isNeedSeries = true;

    public PostType toEntity() {
        return PostType.builder()
                .id(id)
                .name(name)
                .isCanAddComment(isCanAddComment)
                .isCanAddLike(isCanAddLike)
                .isNeedSeries(isNeedSeries)
                .build();
    }
}
