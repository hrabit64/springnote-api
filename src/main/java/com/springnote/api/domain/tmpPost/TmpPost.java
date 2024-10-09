package com.springnote.api.domain.tmpPost;

import com.springnote.api.domain.BaseDateTimeEntity;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;
import com.springnote.api.domain.tmpPostTag.TmpPostTag;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.ColumnDefault;

import java.util.List;

/**
 * 임시 포스트를 나타내는 엔티티 클래스입니다.
 *
 * @auther 황준서 ( hzser123@gmail.com)
 * @since 1.0.0
 */
@EqualsAndHashCode
@NamedEntityGraphs({
        @NamedEntityGraph(name = "TmpPost.detail", attributeNodes = {
                @NamedAttributeNode("series"),
                @NamedAttributeNode("postType"),
                @NamedAttributeNode("tmpPostTags")
        })
})
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "TMP_POST_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "TMP_POST_LAST_MODIFIED_AT"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "TMP_POST")
public class TmpPost extends BaseDateTimeEntity {

    @Id
    @Column(name = "TMP_POST_PK", nullable = false, length = 36)
    private String id;

    @ColumnDefault("제목 없는 포스트")
    @Column(name = "TMP_POST_TITLE", nullable = true, length = 300)
    private String title;

    @ColumnDefault("빈 본문")
    @Column(name = "TMP_POST_CONTENT", nullable = true, columnDefinition = "TEXT")
    private String content;

    @Column(name = "TMP_POST_THUMB", nullable = true, length = 2000)
    private String thumbnail;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SERIES_PK", nullable = true)
    private Series series;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POST_TYPE_PK", nullable = false)
    private PostType postType;

    @OneToMany(mappedBy = "tmpPost", fetch = FetchType.LAZY)
    private List<TmpPostTag> tmpPostTags;

    /**
     * 인자로 주어진 TmpPost 객체의 값으로 해당 객체를 업데이트합니다.
     * title, content, thumbnail, series, postType 을 업데이트합니다.
     *
     * @param tmpPost 업데이트할 정보가 담긴 TmpPost 객체
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public void update(TmpPost tmpPost) {
        this.title = tmpPost.getTitle();
        this.content = tmpPost.getContent();
        this.thumbnail = tmpPost.getThumbnail();
    }


    /**
     * 해당 임시 게시글에 TmpPostTag 를 추가합니다.
     *
     * @param tmpPostTags 추가할 postTags
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public void addTmpPostTag(List<TmpPostTag> postTags) {
        this.tmpPostTags.addAll(postTags);
    }

    /**
     * 해당 임시 게시글에서 TmpPostTag를 삭제합니다.
     *
     * @param postTag 삭제할 tmpPostTag
     * @return 삭제 성공 여부
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public boolean deleteTmpPostTag(TmpPostTag postTag) {
        return this.tmpPostTags.remove(postTag);
    }

    /**
     * 해당 게시글에서 TmpPostTag를 삭제합니다.
     *
     * @param postTag 삭제할 tmpPostTag 들
     * @return 삭제 성공 여부
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public boolean deleteTmpPostTag(List<TmpPostTag> postTag) {
        return this.tmpPostTags.removeAll(postTag);
    }


}
