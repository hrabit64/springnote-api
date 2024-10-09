package com.springnote.api.utils.badWord;

import com.springnote.api.dto.badWord.common.BadWordResponseCommonDto;
import com.springnote.api.service.BadWordService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.ahocorasick.trie.Emit;
import org.ahocorasick.trie.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;


@Slf4j
@RequiredArgsConstructor
@Component
public class AhoCorasickBadWordFilter implements BadWordFilter {
    private final BadWordService badWordService;
    private Trie badTrie;
    private Trie allowTrie;

    @PostConstruct
    public void init() {
        refresh();
    }

    public void refresh() {
        badTrie = fillTrie(false);
        allowTrie = fillTrie(true);
    }

    private Trie fillTrie(Boolean type) {
        var trieBuilder = Trie.builder().ignoreCase();

        //한번에 조회하면 메모리 부담이 크므로 1000개씩 조회
        int offset = 0;
        int limit = 1000;

        Page<BadWordResponseCommonDto> badWords;
        do {
            badWords = badWordService.getAll(PageRequest.of(offset, limit), type);

            if (offset == 0) {
                log.info("badWord filter setup {} total count: {}", (!type) ? "Bad words" : "Allow words", badWords.getTotalElements());
            }
            badWords.forEach(badWord -> trieBuilder.addKeyword(badWord.getWord()));
            log.info("badWord filter setup {} page: {}/{}", (!type) ? "Bad words" : "Allow words", offset, badWords.getTotalPages());
            offset++;

        } while (offset < badWords.getTotalPages() - 1);

        return trieBuilder.build();
    }

    @Override
    public boolean isBadWord(String word) {
        var badWord = badTrie.parseText(word);
        var allowWord = allowTrie.parseText(word);

        if (badWord.isEmpty()) {
            return false;
        }

        if (allowWord.isEmpty()) {
            return true;
        }

        for (var bad : badWord) {
            for (var allow : allowWord) {
                if (!isSameBadWord(bad, allow)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean isSameBadWord(Emit bad, Emit allow) {
        var badWord = bad.getKeyword();
        var allowWord = allow.getKeyword();

        return badWord.contains(allowWord) || allowWord.contains(badWord);
    }
}
