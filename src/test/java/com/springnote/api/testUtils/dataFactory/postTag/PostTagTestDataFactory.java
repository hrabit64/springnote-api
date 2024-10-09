package com.springnote.api.testUtils.dataFactory.postTag;

import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.postTag.PostTag;

import java.util.ArrayList;
import java.util.List;

import static com.springnote.api.testUtils.dataFactory.tag.TagTestDataFactory.createTag;

public class PostTagTestDataFactory {
    public static PostTag createPostTag(Post post) {
        return PostTag.builder()
                .id(1L)
                .post(post)
                .tag(createTag())
                .build();
    }

    private static PostTag createPostTag(Post post, Long id) {
        return PostTag.builder()
                .id(1L)
                .post(post)
                .tag(createTag(id))
                .build();
    }

    public static List<PostTag> createPostTags(Post post, int count) {
        var postTags = new ArrayList<PostTag>();
        for (int i = 0; i < count; i++) {
            postTags.add(createPostTag(post, (long) i));
        }
        return postTags;
    }

    public static PostTag copyPostTag(PostTag postTag) {
        return PostTag.builder()
                .id(postTag.getId())
                .post(postTag.getPost())
                .tag(postTag.getTag())
                .build();
    }
}
