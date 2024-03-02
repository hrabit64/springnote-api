package com.springnote.api.utils.testDataBuilder;


import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.postEditorContent.PostEditorContent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestPostEditorContentBuilder {

    private Long id;
    private String content;
    private Post post;

    public PostEditorContent toEntity() {
        return PostEditorContent.builder()
                .id(id)
                .content(content)
                .post(post)
                .build();
    }
}
