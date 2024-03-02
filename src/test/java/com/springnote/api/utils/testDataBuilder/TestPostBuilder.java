package com.springnote.api.utils.testDataBuilder;


import java.time.LocalDateTime;
import java.util.List;

import com.springnote.api.domain.comment.Comment;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.postEditorContent.PostEditorContent;
import com.springnote.api.domain.postTag.PostTag;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestPostBuilder {

    private Long id;

    @Builder.Default
    private String title = "자동생성된게시글제목";

    @Builder.Default
    private String content = "자동 생성된 게시글 내용";
    
    
    @Builder.Default
    private String thumbnail = "https://thisisfakethumbnailurl.hosisgodgame/image/65";
    
    @Builder.Default
    private boolean isOpen = true;
    
    @Builder.Default
    private Long likeCnt = 0L;

    @Builder.Default
    private Long viewCnt = 0L;

    @Builder.Default
    private LocalDateTime createdDate = LocalDateTime.of(2002,8,28,0,0,0,0);

    @Builder.Default
    private LocalDateTime lastModifiedDate = LocalDateTime.of(2002,8,28,0,0,0,0);

    @Builder.Default
    private Series series = null;
    private PostEditorContent postEditorContent;
    private List<PostTag> postTags;
    private List<Comment> comments;
    private PostType postType;

    //for only test
    @Builder.Default
    private String description = "설명이 없는 테스트 데이터";

    public Post toEntity() {

        return Post.builder()
                .id(id)
                .title(title)
                .content(content)
                .thumbnail(thumbnail)
                .isOpen(isOpen)
                .series(series)
                .postEditorContent(postEditorContent)
                .postTags(postTags)
                .comments(comments)
                .postType(postType)
                .likeCnt(likeCnt)
                .viewCnt(viewCnt)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }

    public Post toEntity(Integer testCaseNum) {
        
        this.title += testCaseNum.toString();

        return Post.builder()
                .id(id)
                .title(title)
                .content(content)
                .thumbnail(thumbnail)
                .isOpen(isOpen)
                .series(series)
                .postEditorContent(postEditorContent)
                .postTags(postTags)
                .comments(comments)
                .postType(postType)
                .likeCnt(likeCnt)
                .viewCnt(viewCnt)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }
}
