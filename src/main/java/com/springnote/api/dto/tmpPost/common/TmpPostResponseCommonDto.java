package com.springnote.api.dto.tmpPost.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.tmpPost.TmpPost;
import com.springnote.api.domain.tmpPostTag.TmpPostTag;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.dto.series.common.SeriesSimpleResponseDto;
import com.springnote.api.dto.tag.common.TagResponseDto;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Relation(collectionRelation = "tmp_posts")
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TmpPostResponseCommonDto {

    private String id;
    private String title;
    private String content;
    private String thumbnail;
    private SeriesSimpleResponseDto series;
    private List<TagResponseDto> tags;
    private PostTypeResponseDto postType;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    public TmpPostResponseCommonDto(TmpPost tmpPost) {

        this.id = tmpPost.getId();
        this.title = tmpPost.getTitle();
        this.content = tmpPost.getContent();
        this.thumbnail = tmpPost.getThumbnail();
        this.series = (tmpPost.getSeries() != null) ? new SeriesSimpleResponseDto(tmpPost.getSeries()) : null;
        this.tags = (tmpPost.getTmpPostTags() == null || tmpPost.getTmpPostTags().isEmpty()) ? Collections.emptyList()
                : tmpPost.getTmpPostTags().stream().map(TmpPostTag::getTag).map(TagResponseDto::new).toList();
        this.postType = new PostTypeResponseDto(tmpPost.getPostType());
        this.createdAt = tmpPost.getCreatedDate();
        this.lastUpdatedAt = tmpPost.getLastModifiedDate();

    }
}
