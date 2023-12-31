package com.springnote.api.dto.post.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.jpa.post.Post;
import com.springnote.api.dto.series.common.SeriesResponseDto;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import java.time.LocalDateTime;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy.class)
@Relation(collectionRelation = "post_items")
public class PostResponseDto {
    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;
    private String thumbnail;
    private SeriesResponseDto series;

    public PostResponseDto(Post post){
        id = post.getId();
        title = post.getTitle();
        createdAt = post.getCreateAt();
        updateAt = post.getUpdateAt();
        thumbnail = post.getThumbnail();
        series = new SeriesResponseDto(post.getSeries());
    }
}
