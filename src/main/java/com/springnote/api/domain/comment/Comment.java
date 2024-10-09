package com.springnote.api.domain.comment;

import com.springnote.api.domain.BaseDateTimeEntity;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.user.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

/**
 * 댓글을 나타내는 엔티티 클래스입니다.
 *
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@NamedEntityGraphs({
        @NamedEntityGraph(name = "Comment.post", attributeNodes = @NamedAttributeNode("post")),
        @NamedEntityGraph(name = "Comment.reply", attributeNodes = @NamedAttributeNode("reply")),
        @NamedEntityGraph(name = "Comment.user", attributeNodes = @NamedAttributeNode("user"))
})
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "COMMENT_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "COMMENT_LAST_MODIFIED_AT"))
})
@Getter
@Setter
@ToString
@EqualsAndHashCode(callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "COMMENT")
public class Comment extends BaseDateTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_PK")
    private Long id;

    @Column(name = "COMMENT_CONTENT", length = 1000)
    private String content;

    @Column(name = "COMMENT_IS_ENABLED")
    private boolean isEnabled;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POST_PK")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "COMMENT_PARENT")
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
    private List<Comment> reply;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_PK")
    private User user;

    @Column(name = "COMMENT_IP", length = 15)
    private String ip;


    public void update(Comment comment) {
        this.content = comment.getContent();
    }

    public boolean isReply() {
        return parent != null;
    }
}
