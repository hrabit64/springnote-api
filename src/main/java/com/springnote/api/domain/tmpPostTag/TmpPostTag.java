package com.springnote.api.domain.tmpPostTag;


import com.springnote.api.domain.PostAndTag;
import com.springnote.api.domain.tag.Tag;
import com.springnote.api.domain.tmpPost.TmpPost;
import jakarta.persistence.*;
import lombok.*;

/**
 * 임시 게시글의 태그를 나타내는 엔티티 클래스입니다.
 *
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@NamedEntityGraphs({
        @NamedEntityGraph(name = "TmpPostTag.tag", attributeNodes = @NamedAttributeNode("tag")),
})
@Getter
@EqualsAndHashCode(of = "id")
@Setter
@ToString(exclude = {"tmpPost"})
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "TMP_POST_TAG")
public class TmpPostTag implements PostAndTag {

    @Id
    @Column(name = "TMP_POST_TAG_PK", nullable = false)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TMP_POST_PK", nullable = false)
    private TmpPost tmpPost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TAG_PK", nullable = false)
    private Tag tag;
}
