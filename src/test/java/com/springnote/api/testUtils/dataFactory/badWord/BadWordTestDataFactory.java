package com.springnote.api.testUtils.dataFactory.badWord;

import com.springnote.api.domain.badWord.BadWord;
import com.springnote.api.testUtils.RandomStringGenerator;

public class BadWordTestDataFactory {

    public static BadWord createStandardBadWord(boolean type) {
        return BadWord.builder()
                .id(1L)
                .word(RandomStringGenerator.generateRandomString(10))
                .type(type)
                .build();
    }
}
