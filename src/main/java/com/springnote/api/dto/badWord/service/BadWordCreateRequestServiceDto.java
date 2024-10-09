package com.springnote.api.dto.badWord.service;


import com.springnote.api.domain.badWord.BadWord;
import lombok.*;

@EqualsAndHashCode
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BadWordCreateRequestServiceDto {
    private String word;
    private Boolean isBadWord;

    public BadWord toEntity() {
        return BadWord.builder()
                .word(word)
                .type(isBadWord)
                .build();
    }
}
