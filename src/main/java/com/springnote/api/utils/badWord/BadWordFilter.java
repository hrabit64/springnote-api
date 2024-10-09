package com.springnote.api.utils.badWord;

import org.springframework.stereotype.Component;

@Component
public interface BadWordFilter {
    boolean isBadWord(String word);

    void refresh();
}
