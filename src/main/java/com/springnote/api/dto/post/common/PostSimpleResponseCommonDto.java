package com.springnote.api.dto.post.common;

import java.time.LocalDateTime;
import java.util.List;

import com.springnote.api.domain.post.Post;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.dto.series.common.SeriesSimpleResponseDto;
import com.springnote.api.dto.tag.common.TagResponseDto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostSimpleResponseCommonDto {
    private Long id;
    private String title;
    private String content;
    private String thumbnail;
    private Long views;
    private Long likes;
    private SeriesSimpleResponseDto series;
    private List<TagResponseDto> tags;
    private PostTypeResponseDto postType;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private boolean isOpen;

    public PostSimpleResponseCommonDto(Post post){
    }
}
