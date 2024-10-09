package com.springnote.api.dto.post.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.postTag.PostTag;
import com.springnote.api.dto.content.controller.ContentResponseControllerDto;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.dto.series.common.SeriesSimpleResponseDto;
import com.springnote.api.dto.tag.common.TagResponseDto;
import lombok.*;
import org.jetbrains.annotations.NotNull;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Relation(collectionRelation = "posts")
@EqualsAndHashCode
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostDetailResponseCommonDto {

    private Long id;
    private String title;
    private ContentResponseControllerDto content;
    private String thumbnail;
    private SeriesSimpleResponseDto series;
    private List<TagResponseDto> tags;
    private PostTypeResponseDto postType;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;

    @JsonProperty("enabled")
    private boolean isEnabled;

    public PostDetailResponseCommonDto(Post post) {

        this.id = post.getId();
        this.title = post.getTitle();

        this.thumbnail = post.getThumbnail();
        this.series = (post.getSeries() != null) ? new SeriesSimpleResponseDto(post.getSeries()) : null;
        this.tags = (post.getPostTags() == null || post.getPostTags().isEmpty()) ? Collections.emptyList() : getPostTagList(post);

        this.postType = new PostTypeResponseDto(post.getPostType());
        this.createdAt = post.getCreatedDate();
        this.lastUpdatedAt = post.getLastModifiedDate();
        this.isEnabled = post.isEnabled();

        this.content = new ContentResponseControllerDto(post.getContent());

    }

    private @NotNull List<TagResponseDto> getPostTagList(Post post) {
        return post.getPostTags().stream().map(PostTag::getTag).map(TagResponseDto::new).toList();
    }
}
