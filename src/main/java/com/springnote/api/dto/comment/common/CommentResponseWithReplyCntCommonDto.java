package com.springnote.api.dto.comment.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.comment.Comment;
import com.springnote.api.dto.user.controller.UserSimpleResponseControllerDto;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@Relation(collectionRelation = "comments")
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CommentResponseWithReplyCntCommonDto {

    private Long id;
    private Long postId;
    private String content;
    private UserSimpleResponseControllerDto writer;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Long replyCount;
    private boolean enabled;

    public CommentResponseWithReplyCntCommonDto(Comment comment, Long replyCount) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();
        this.content = (comment.isEnabled()) ? comment.getContent() : "삭제된 댓글입니다.";
        this.writer = new UserSimpleResponseControllerDto(comment.getUser());
        this.createdDate = comment.getCreatedDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.replyCount = replyCount;
        this.enabled = comment.isEnabled();
    }

    public CommentResponseWithReplyCntCommonDto(Comment comment, Long replyCount, boolean isAdmin) {
        this.id = comment.getId();
        this.postId = comment.getPost().getId();

        if (isAdmin) {
            this.content = comment.getContent();
        } else {
            this.content = (comment.isEnabled()) ? comment.getContent() : "삭제된 댓글입니다.";
        }
        this.writer = new UserSimpleResponseControllerDto(comment.getUser());
        this.createdDate = comment.getCreatedDate();
        this.lastModifiedDate = comment.getLastModifiedDate();
        this.replyCount = replyCount;
        this.enabled = comment.isEnabled();
    }
}
