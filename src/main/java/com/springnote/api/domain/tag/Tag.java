package com.springnote.api.domain.tag;

import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.postTag.PostTag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 태그를 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "TAG")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TAG_PK", nullable = false)
    private Long id;

    @Column(name = "TAG_NM", nullable = false, updatable = false, unique = true, length = 100)
    private String name;

    public PostTag toPostTag(Post post) {
        return PostTag.builder()
                .post(post)
                .tag(this)
                .build();
    }
}
