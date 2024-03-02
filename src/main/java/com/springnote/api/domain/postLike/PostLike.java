package com.springnote.api.domain.postLike;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;

import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.user.User;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 게시글의 좋아요를 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@NamedEntityGraphs({
        @NamedEntityGraph(name = "PostLike.post", attributeNodes = @NamedAttributeNode("post")),
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "POST_LIKE")
public class PostLike {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_LIKE_PK", nullable = false)
    private Long id;

    @CreatedDate
    @Column(name = "POST_LIKE_CREATED_AT", updatable = false ,nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_PK", nullable = false)
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PK", nullable = false)
    private User user;

}
