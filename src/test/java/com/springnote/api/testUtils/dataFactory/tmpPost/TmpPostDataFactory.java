package com.springnote.api.testUtils.dataFactory.tmpPost;

import com.springnote.api.domain.tmpPost.TmpPost;
import com.springnote.api.testUtils.RandomStringGenerator;
import com.springnote.api.testUtils.dataFactory.postTag.TmpPostTagTestDataFactory;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static com.springnote.api.testUtils.dataFactory.postType.PostTypeTestDataFactory.createPostType;
import static com.springnote.api.testUtils.dataFactory.series.SeriesTestDataFactory.createSeries;

public class TmpPostDataFactory {

    public static TmpPost createTmpPost(String id) {
        var tmpPost = TmpPost.builder()
                .id(id)
                .title(RandomStringGenerator.generateRandomString(10))
                .content(RandomStringGenerator.generateRandomString(10))
                .thumbnail("thumbnail")
                .series(createSeries())
                .createdDate(testLocalDateTime())
                .lastModifiedDate(testLocalDateTime())
                .postType(createPostType())
                .build();

        var tmpPostTags = TmpPostTagTestDataFactory.createPostTags(tmpPost, 3);

        tmpPost.setTmpPostTags(tmpPostTags);

        return tmpPost;
    }

    public static TmpPost copyTmpPost(TmpPost tmpPost) {
        return TmpPost.builder()
                .id(tmpPost.getId())
                .title(tmpPost.getTitle())
                .content(tmpPost.getContent())
                .thumbnail(tmpPost.getThumbnail())
                .series(tmpPost.getSeries())
                .createdDate(tmpPost.getCreatedDate())
                .lastModifiedDate(tmpPost.getLastModifiedDate())
                .postType(tmpPost.getPostType())
                .tmpPostTags(tmpPost.getTmpPostTags())
                .build();
    }


}
