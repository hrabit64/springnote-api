package com.springnote.api.dto.post.service;

import com.springnote.api.domain.content.Content;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;
import lombok.*;

import java.util.List;

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
    private String thumbnail;
    private Long postTypeId;
    private boolean isEnabled;

    public Post toEntity(Series series, PostType postType, Content content) {
        return Post.builder()
                .postType(postType)
                .series(series)
                .title(title)
                .content(content)
                .thumbnail(thumbnail)
                .isEnabled(isEnabled)
                .build();
    }
}
