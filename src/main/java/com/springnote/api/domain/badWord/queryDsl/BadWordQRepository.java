package com.springnote.api.domain.badWord.queryDsl;

import com.springnote.api.domain.badWord.BadWord;
import org.jetbrains.annotations.Nullable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BadWordQRepository {
    /**
     * 주어진 단어로 금칙어를 검색합니다. (FullText Match)
     *
     * @param word     검색할 단어
     * @param type     금칙어 유형(true: 허용, false: 금지)
     * @param pageable the pageable
     * @return the page
     */
    Page<BadWord> matchByWord(String word, @Nullable Boolean type, Pageable pageable);
}
