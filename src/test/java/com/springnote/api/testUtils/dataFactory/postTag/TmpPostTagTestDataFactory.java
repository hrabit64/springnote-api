package com.springnote.api.testUtils.dataFactory.postTag;

import com.springnote.api.domain.tag.Tag;
import com.springnote.api.domain.tmpPost.TmpPost;
import com.springnote.api.domain.tmpPostTag.TmpPostTag;

import java.util.ArrayList;
import java.util.List;

import static com.springnote.api.testUtils.dataFactory.tag.TagTestDataFactory.createTag;

public class TmpPostTagTestDataFactory {

    public static TmpPostTag createTmpPostTag(TmpPost post, Long id) {
        return TmpPostTag.builder()
                .id(id)
                .tmpPost(post)
                .tag(createTag(id))
                .build();

    }

    public static TmpPostTag createTmpPostTag(TmpPost post, Tag tag) {
        return TmpPostTag.builder()
                .tmpPost(post)
                .tag(tag)
                .build();
    }

    public static List<TmpPostTag> createPostTags(TmpPost post, int count) {
        var postTags = new ArrayList<TmpPostTag>();
        for (int i = 0; i < count; i++) {
            postTags.add(createTmpPostTag(post, (long) i));
        }
        return postTags;
    }
}
