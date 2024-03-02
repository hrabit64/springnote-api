package com.springnote.api.domain.post;


import java.time.LocalDateTime;
import java.util.List;

import com.springnote.api.domain.comment.Comment;
import com.springnote.api.domain.postEditorContent.PostEditorContent;
import com.springnote.api.domain.postTag.PostTag;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;
import com.springnote.api.domain.tag.Tag;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 게시글 정보를 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 ( hzser123@gmail.com)
 * @since 1.0.0
 */
@NamedEntityGraphs({
        @NamedEntityGraph(name = "detail", attributeNodes = {
            @NamedAttributeNode("series"),
            @NamedAttributeNode("postType"),
            @NamedAttributeNode("postEditorContent"),
            @NamedAttributeNode("postTags"),
            @NamedAttributeNode("comments")
        }),
        @NamedEntityGraph(name = "simple", attributeNodes = {
            @NamedAttributeNode("series"),
            @NamedAttributeNode("postType"),
            @NamedAttributeNode("postTags")
        })
})
@ToString(exclude = {"postEditorContent", "postTags", "comments"})
@EqualsAndHashCode(of = "id")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "POST")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_PK", nullable = false)
    private Long id;

    @Column(name = "POST_TITLE", nullable = false, length = 300)
    private String title;

    // 해당 content는 markdown 태그가 모두 제거된 plain text 임.
    // markdown 태그가 포함된 content는 PostEditorContent 객체를 통해 접근할 것.
    @Column(name = "POST_CONTENT", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "POST_THUMB", nullable = false, length = 2000)
    private String thumbnail;

    @Column(name = "POST_IS_OPEN", nullable = false)
    private boolean isOpen;

    @Column(name = "POST_VIEW_CNT", nullable = false)
    private Long viewCnt;

    @Column(name = "POST_LIKE_CNT", nullable = false)
    private Long likeCnt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERIES_PK", nullable = true)
    private Series series;

    @Column(name = "POST_LAST_UPDATED_AT")
    private LocalDateTime lastModifiedDate;
    
    @Column(name = "POST_CREATED_AT", updatable = false)
    private LocalDateTime createdDate;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_TYPE_PK", nullable = false)
    private PostType postType;

    // markdown 태그가 포함된 content
    @OneToOne(mappedBy = "post", fetch = FetchType.LAZY, orphanRemoval = true, cascade = CascadeType.ALL)
    private PostEditorContent postEditorContent;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<PostTag> postTags;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    private List<Comment> comments;


    
    /**
     * 인자로 주어진 Post 객체의 값으로 해당 객체를 업데이트합니다.
     * 
     * title, content, thumbnail, postStatus, series 정보를 업데이트합니다.
     * 
     * @param post 업데이트할 정보가 담긴 Post 객체
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    public void update(Post post){
        this.title = post.getTitle();
        this.content = post.getContent();
        this.thumbnail = post.getThumbnail();
        this.series = post.getSeries();
    }


    /**
     * 해당 게시글의 조회수를 1 증가시킵니다.
     * 
     * @auther 황준서 ( hzser123@gmail.com)
     * @since 1.0.0
     */
    public void updateViewCnt(){
        this.viewCnt++;
    }


    /**
     * 해당 게시글에 Tag를 추가합니다.
     * 
     * @param tag 추가할 tag
     * 
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public void addTag(Tag tag){
        var postTag = PostTag.builder()
                .post(this)
                .tag(tag)
                .build();
        this.postTags.add(postTag);
    }


    /**
     * 해당 게시글에서 Tag를 삭제합니다.
     * 
     * @param tag 삭제할 tag
     * @return 삭제 성공 여부
     * 
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public boolean deleteTag(Tag tag){
        return this.postTags.removeIf(postTag -> postTag.getTag().equals(tag));
    }
}