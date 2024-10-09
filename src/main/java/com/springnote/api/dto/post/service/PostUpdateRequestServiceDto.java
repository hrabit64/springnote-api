package com.springnote.api.dto.post.service;

import lombok.*;

import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostUpdateRequestServiceDto {
    private Long id;
    private Long seriesId;
    private List<Long> tagIds;
    private String content;
    private String title;
    private String thumbnail;
    private boolean isEnabled;
}
