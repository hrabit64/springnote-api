package com.springnote.api.domain.badWord;

import com.springnote.api.domain.badWord.queryDsl.BadWordQRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BadWordRepository extends JpaRepository<BadWord, Long>, BadWordQRepository {

    /**
     * 금칙어를 금칙어 유형에 따라 검색합니다.
     *
     * @param type     금칙어 유형(true: 허용, false: 금지)
     * @param pageable the pageable
     * @return the page
     */
    Page<BadWord> findBadWordByType(Boolean type, Pageable pageable);

    /**
     * 주어진 단어로 완전히 일치하는 금칙어가 있는지 확인합니다.(유형 상관 없이)
     *
     * @param word 검색할 단어
     * @return the boolean
     */
    boolean existsByWord(String word);

    /**
     * 주어진 단어를 포함하는 금칙어가 있는지 확인합니다.(유형 상관 없이)
     *
     * @param word 검색할 단어
     * @return the boolean
     */
    Page<BadWord> findByWordContaining(String word, Pageable pageable);

    /**
     * 주어진 단어로 완전히 일치하는 금칙어가 있는지 확인합니다.
     *
     * @param word 검색할 단어
     * @param type 금칙어 유형(true: 허용, false: 금지)
     * @return the boolean
     */
    Page<BadWord> findByWordContainingAndType(String word, Boolean type, Pageable pageable);
}
