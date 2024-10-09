package com.springnote.api.domain.badWord;

import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "BAD_WORD")
public class BadWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "BAD_WORD_PK")
    private Long id;

    @Column(name = "BAD_WORD_CONTENT", unique = true, nullable = false, length = 15)
    private String word;

    /**
     * 금칙어 유형(true: 허용, false: 금지)
     */
    @Column(name = "BAD_WORD_TYPE", nullable = false)
    private Boolean type;
}
