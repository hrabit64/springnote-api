package com.springnote.api.dto.badWord.controller;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.dto.badWord.service.BadWordCreateRequestServiceDto;
import com.springnote.api.utils.validation.string.CheckHasBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@EqualsAndHashCode
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BadWordCreateRequestControllerDto {

    @CheckHasBlank
    @NotEmpty(message = "금칙어를 입력해주세요.")
    @Size(min = 2, max = 15, message = "금칙어는 2자 이상, 15자 이하여야 합니다.")
    private String word;

    @NotNull(message = "금칙어 여부를 입력해주세요.")
    private Boolean isBadWord;

    public BadWordCreateRequestServiceDto toServiceDto() {
        return BadWordCreateRequestServiceDto.builder()
                .word(word)
                .isBadWord(isBadWord)
                .build();
    }
}
