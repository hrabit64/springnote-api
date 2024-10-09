package com.springnote.api.dto.comment.service;

import com.springnote.api.domain.comment.Comment;
import com.springnote.api.domain.user.User;
import lombok.*;

@EqualsAndHashCode
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReplyCreateRequestServiceDto {
    private Long postId;
    private Long parentId;
    private String content;
    private String ip;


    public Comment toEntity(Comment parent, User user, String filteredContent) {
        return Comment.builder()
                .post(parent.getPost())
                .content(filteredContent)
                .ip(ip)
                .user(user)
                .parent(parent)
                .isEnabled(true)
                .build();
    }
}
