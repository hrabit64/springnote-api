package com.springnote.api.testUtils.dataFactory.badWord;

import com.springnote.api.dto.badWord.common.BadWordResponseCommonDto;

public class BadWordDtoTestDataFactory {

    public static BadWordResponseCommonDto createBadWordResponseDto() {
        return BadWordResponseCommonDto.builder()
                .id(1L)
                .word("word")
                .isBadWord(true)
                .build();
    }

    public static BadWordResponseCommonDto createBadWordResponseDto(boolean isBadWord) {
        return BadWordResponseCommonDto.builder()
                .id(1L)
                .word("word")
                .isBadWord(isBadWord)
                .build();
    }
}
