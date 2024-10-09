package com.springnote.api.testUtils.dataFactory.postType;

import com.springnote.api.domain.postType.PostType;

public class PostTypeTestDataFactory {

    public static PostType createPostType() {
        return PostType.builder()
                .id(1L)
                .name("testname")
                .build();
    }

    public static PostType createAddCommentOKPostType() {
        return PostType.builder()
                .id(1L)
                .name("comment")
                .isCanAddComment(true)
                .build();
    }

    public static PostType createSeriesPostType(boolean isNeedSeries) {
        return PostType.builder()
                .id(1L)
                .name("series")
                .isNeedSeries(isNeedSeries)
                .build();
    }

}
