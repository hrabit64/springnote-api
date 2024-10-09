package com.springnote.api.domain.content;

import jakarta.persistence.*;
import lombok.*;


/**
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@ToString
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "CONTENT")
public class Content {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTENT_PK", nullable = false)
    private Long id;

    @Column(name = "CONTENT_PLAIN_TEXT", nullable = false, columnDefinition = "TEXT", length = 30000)
    private String plainText;

    @Column(name = "CONTENT_EDITOR_TEXT", nullable = false, columnDefinition = "TEXT", length = 30000)
    private String editorText;

}
