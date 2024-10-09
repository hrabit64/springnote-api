package com.springnote.api.dto.tmpPost.service;

import java.util.List;

import com.springnote.api.domain.tmpPost.TmpPost;

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
public class TmpPostUpdateRequestServiceDto {
    private String id;
    private Long seriesId;
    private List<Long> tagIds;
    private String content;
    private String title;
    private String thumbnail;

    public TmpPost toEntity() {
        return TmpPost.builder()
                .content(content)
                .title(title)
                .thumbnail(thumbnail)
                .build();
    }
}
