package com.springnote.api.dto.post.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.post.Post;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.dto.series.common.SeriesSimpleResponseDto;
import com.springnote.api.dto.tag.common.TagResponseDto;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.List;

@Relation(collectionRelation = "posts")
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostSimpleResponseCommonDto {
    private Long id;
    private String title;
    private String thumbnail;
    private SeriesSimpleResponseDto series;
    private List<TagResponseDto> tags;
    private PostTypeResponseDto postType;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private boolean isEnabled;

    public PostSimpleResponseCommonDto(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.thumbnail = post.getThumbnail();
        this.series = (post.getSeries() != null) ? new SeriesSimpleResponseDto(post.getSeries()) : null;
        this.tags = (!post.getPostTags().isEmpty()) ? getPostTags(post) : List.of();
        this.postType = new PostTypeResponseDto(post.getPostType());
        this.createdAt = post.getCreatedDate();
        this.lastUpdatedAt = post.getLastModifiedDate();
        this.isEnabled = post.isEnabled();

    }

    private @NotNull List<TagResponseDto> getPostTags(Post post) {
        return post.getPostTags().stream().map(postTag -> new TagResponseDto(postTag.getTag())).toList();
    }
}
