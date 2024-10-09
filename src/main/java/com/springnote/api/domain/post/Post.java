package com.springnote.api.domain.post;

import com.springnote.api.domain.BaseDateTimeEntity;
import com.springnote.api.domain.content.Content;
import com.springnote.api.domain.postTag.PostTag;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 게시글 정보를 나타내는 엔티티 클래스입니다.
 *
 * @auther 황준서 ( hzser123@gmail.com)
 * @since 1.0.0
 */
@NamedEntityGraphs({
        @NamedEntityGraph(name = "Post.detail", attributeNodes = {
                @NamedAttributeNode("series"),
                @NamedAttributeNode("postType"),
                @NamedAttributeNode("content"),
                @NamedAttributeNode("postTags")
        }),
        @NamedEntityGraph(name = "Post.simple", attributeNodes = {
                @NamedAttributeNode("series"),
                @NamedAttributeNode("postType"),
                @NamedAttributeNode("postTags")
        })
})
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "POST_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "POST_LAST_MODIFIED_AT"))
})
@ToString(exclude = {"postTags", "comments"})
@EqualsAndHashCode(of = "id", callSuper = false)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "POST")
public class Post extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "POST_PK", nullable = false)
    private Long id;

    @Column(name = "POST_TITLE", nullable = false, length = 300)
    private String title;

    @Column(name = "POST_THUMB", nullable = false, length = 2000)
    private String thumbnail;

    @Column(name = "POST_IS_ENABLE", nullable = false)
    private boolean isEnabled;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERIES_PK", nullable = true)
    private Series series;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_TYPE_PK", nullable = false)
    private PostType postType;

    // markdown 태그가 포함된 content
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_PK", nullable = false)
    private Content content;

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY)
    private List<PostTag> postTags;

    public boolean deletePostTag(List<PostTag> postTag) {
        return this.postTags.removeAll(postTag);
    }

    public void addPostTag(PostTag postTag) {
        this.postTags.add(postTag);
    }

    public void addPostTags(List<PostTag> postTags) {
        if (this.postTags == null || this.postTags.isEmpty()) {
            this.postTags = postTags;
            return;
        }
        this.postTags.addAll(postTags);
    }
}