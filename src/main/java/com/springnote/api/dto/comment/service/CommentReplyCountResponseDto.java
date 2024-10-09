package com.springnote.api.dto.comment.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CommentReplyCountResponseDto {
    private Long id;
    private Long replyCount;
}


