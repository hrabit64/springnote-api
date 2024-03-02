package com.springnote.api.dto.post.service;

import java.util.List;

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
public class PostCreateRequestServiceDto {
    private Long seriesId;
    private List<Long> tagIds;
    
    private String content;
    private String title;
}
