package com.springnote.api.dto.comment.service;

import com.springnote.api.domain.comment.Comment;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.user.User;
import lombok.*;

@EqualsAndHashCode
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentCreateRequestServiceDto {
    private Long postId;
    private String content;
    private String ip;

    public Comment toEntity(Post post, User user, String filteredContent) {
        return Comment.builder()
                .post(post)
                .content(filteredContent)
                .user(user)
                .isEnabled(true)
                .ip(ip)
                .build();

    }

}
