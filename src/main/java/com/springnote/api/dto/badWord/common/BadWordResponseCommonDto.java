package com.springnote.api.dto.badWord.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.badWord.BadWord;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "bad_words")
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BadWordResponseCommonDto {

    private Long id;
    private String word;
    private Boolean isBadWord;

    public BadWordResponseCommonDto(BadWord badWord) {
        this.id = badWord.getId();
        this.word = badWord.getWord();
        this.isBadWord = badWord.getType();
    }

}
