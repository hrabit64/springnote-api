package com.springnote.api.dto.comment.service;

import com.springnote.api.domain.comment.Comment;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentUpdateRequestServiceDto {

    private Long id;
    private String content;

    public Comment toEntity(String filteredContent) {
        return Comment.builder()
                .content(filteredContent)
                .build();
    }
}
