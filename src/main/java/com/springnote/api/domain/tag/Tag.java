package com.springnote.api.domain.tag;

import com.springnote.api.domain.BaseDateTimeEntity;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.postTag.PostTag;
import com.springnote.api.domain.tmpPost.TmpPost;
import com.springnote.api.domain.tmpPostTag.TmpPostTag;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * 태그를 나타내는 엔티티 클래스입니다.
 *
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "TAG_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "TAG_LAST_MODIFIED_AT"))
})
@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id", callSuper = false)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "TAG")
public class Tag extends BaseDateTimeEntity {

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

    public TmpPostTag toTmpPostTag(TmpPost post) {
        return TmpPostTag.builder()
                .tmpPost(post)
                .tag(this)
                .build();
    }

    public void update(Tag tag) {
        this.name = tag.getName();
    }
}
