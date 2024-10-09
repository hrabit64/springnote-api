package com.springnote.api.dto.tmpPost.service;

import java.util.List;

import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;
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
public class TmpPostCreateRequestServiceDto {
    private Long seriesId;
    private List<Long> tagIds;
    private String content;
    private String title;
    private String thumbnail;
    private Long postTypeId;

    public TmpPost toEntity(Series series, PostType postType) {
        return TmpPost.builder()
                .series(series)
                .content(content)
                .title(title)
                .thumbnail(thumbnail)
                .postType(postType)
                .build();
    }
}
