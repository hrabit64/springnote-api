package com.springnote.api.domain.postEditorContent;

import com.springnote.api.domain.post.Post;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.NamedEntityGraphs;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * 게시글의 에디터 컨텐츠(markdown content) 를 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@NamedEntityGraphs({
        @NamedEntityGraph(name = "PostEditorContent.post", attributeNodes = @NamedAttributeNode("post")),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "POST_EDITOR_CONTENT")
public class PostEditorContent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_EDITOR_CONTENT_PK", nullable = false)
    private Long id;
    
    @Column(name = "POST_EDITOR_CONTENT_TEXT", nullable = false, columnDefinition = "TEXT")
    private String content;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_PK", nullable = false)
    private Post post;
    
}
