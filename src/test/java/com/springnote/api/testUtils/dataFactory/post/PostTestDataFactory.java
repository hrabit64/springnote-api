package com.springnote.api.testUtils.dataFactory.post;

import com.springnote.api.domain.content.Content;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.testUtils.RandomStringGenerator;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static com.springnote.api.testUtils.dataFactory.postTag.PostTagTestDataFactory.createPostTags;

public class PostTestDataFactory {
    public static Post createPost() {
        return Post.builder()
                .id(1L)
                .title(RandomStringGenerator.generateRandomString(10))
                .content(Content.builder()
                        .editorText(RandomStringGenerator.generateRandomString(6))
                        .plainText(RandomStringGenerator.generateRandomString(6))
                        .build()
                )
                .postType(PostType.builder().build())
                .isEnabled(true)
                .thumbnail("thumbnail")
                .createdDate(testLocalDateTime())
                .lastModifiedDate(testLocalDateTime())
                .build();
    }

    public static Post createPost(boolean isEnabled) {
        return Post.builder()
                .id(1L)
                .title(RandomStringGenerator.generateRandomString(10))
                .content(Content.builder()
                        .editorText(RandomStringGenerator.generateRandomString(6))
                        .plainText(RandomStringGenerator.generateRandomString(6))
                        .build()
                )
                .postType(PostType.builder().build())
                .isEnabled(isEnabled)
                .thumbnail("thumbnail")
                .createdDate(testLocalDateTime())
                .lastModifiedDate(testLocalDateTime())
                .build();
    }

    public static Post createPost(PostType postType) {
        return Post.builder()
                .id(1L)
                .title(RandomStringGenerator.generateRandomString(10))
                .content(Content.builder()
                        .editorText(RandomStringGenerator.generateRandomString(6))
                        .plainText(RandomStringGenerator.generateRandomString(6))
                        .build()
                )
                .postType(postType)
                .isEnabled(true)
                .thumbnail("thumbnail")
                .createdDate(testLocalDateTime())
                .lastModifiedDate(testLocalDateTime())
                .build();
    }

    public static Post createPost(Long id) {
        return Post.builder()
                .id(id)
                .title(RandomStringGenerator.generateRandomString(10))
                .content(Content.builder()
                        .editorText(RandomStringGenerator.generateRandomString(6))
                        .plainText(RandomStringGenerator.generateRandomString(6))
                        .build()
                )
                .postType(PostType.builder().id(1L).build())
                .isEnabled(true)
                .thumbnail("thumbnail")
                .createdDate(testLocalDateTime())
                .lastModifiedDate(testLocalDateTime())
                .build();
    }

    public static Post createFullyPost(Long id) {
        var post = PostTestDataBuilder.builder()
                .id(id)
                .build()
                .toPost();

        var postTags = createPostTags(post, 3);

        post.setPostTags(postTags);

        return post;
    }

    public static Post createFullyPostWithTitle(String title) {
        var post = PostTestDataBuilder.builder()
                .title(title)
                .build()
                .toPost();

        var postTags = createPostTags(post, 3);

        post.setPostTags(postTags);

        return post;
    }

    public static Post copyPost(Post post) {
        return Post.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .postType(post.getPostType())
                .isEnabled(post.isEnabled())
                .thumbnail(post.getThumbnail())
                .createdDate(post.getCreatedDate())
                .lastModifiedDate(post.getLastModifiedDate())
                .postTags(post.getPostTags())
                .build();
    }

}
