package com.springnote.api.dto.postType.common;

import com.springnote.api.domain.postType.PostType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "post_types")
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostTypeResponseDto {
    private Long id;
    private String name;

    public PostTypeResponseDto(PostType postType) {
        this.id = postType.getId();
        this.name = postType.getName();
    }
}
