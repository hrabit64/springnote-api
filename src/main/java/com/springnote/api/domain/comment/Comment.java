package com.springnote.api.domain.comment;

import java.util.List;
import com.springnote.api.domain.BaseDateTimeEntity;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.user.User;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
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
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;


/**
 * 댓글을 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@NamedEntityGraphs({
        @NamedEntityGraph(name = "Comment.post", attributeNodes = @NamedAttributeNode("post")),
        @NamedEntityGraph(name = "Comment.user", attributeNodes = @NamedAttributeNode("user")),
        @NamedEntityGraph(name = "Comment.reply", attributeNodes = @NamedAttributeNode("reply"))
})
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "COMMENT_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "COMMENT_LAST_UPDATED_AT"))
})
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "COMMENT")
public class Comment extends BaseDateTimeEntity{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "COMMENT_PK")
    private Long id;

    @Column(name = "COMMENT_CONTENT")
    private String content;

    @Column(name = "COMMENT_IS_OPEN")
    private boolean isOpen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "POST_PK")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_PK")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "COMMENT_PARENT")
    private Comment parent;

    @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY,cascade = CascadeType.PERSIST, orphanRemoval = true)
    private List<Comment> reply;

}
