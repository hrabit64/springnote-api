package com.springnote.api.dto.tag.common;

import com.springnote.api.domain.tag.Tag;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "tags")
@EqualsAndHashCode
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TagResponseDto {
    private Long id;
    private String name;

    public TagResponseDto(Tag tag) {
        this.id = tag.getId();
        this.name = tag.getName();
    }
}
