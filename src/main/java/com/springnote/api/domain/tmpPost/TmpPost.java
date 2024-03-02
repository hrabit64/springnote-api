package com.springnote.api.domain.tmpPost;

import org.hibernate.annotations.ColumnDefault;
import com.springnote.api.domain.BaseDateTimeEntity;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;
import com.springnote.api.utils.exception.general.ValidationException;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 임시 포스트를 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 ( hzser123@gmail.com)
 * @since 1.0.0
 */
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "TMP_POST_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "TMP_POST_LAST_UPDATED_AT"))
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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


    /**
     * 인자로 주어진 TmpPost 객체의 값으로 해당 객체를 업데이트합니다.
     * title, content, thumbnail, series, postType 을 업데이트합니다.
     * 
     * @param tmpPost 업데이트할 정보가 담긴 TmpPost 객체
     * 
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public void update(TmpPost tmpPost){
        this.title = tmpPost.getTitle();
        this.content = tmpPost.getContent();
        this.thumbnail = tmpPost.getThumbnail();
        this.series = tmpPost.getSeries();
        this.postType = tmpPost.getPostType();
    }


    /**
     * 해당 임시 포스트가 유효한지 검사합니다.
     * 유효할 경우 true 를 리턴하지만, 유효하지 않을 경우 ValidationException 을 발생시킵니다.
     * 
     * @see ValidationException
     * @throws ValidationException
     * @return 유효성 여부
     * 
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public boolean isValid(){
        if(this.title == null || this.title.isEmpty()){
            throw new ValidationException("제목이 없습니다.");
        }
        if(this.content == null || this.content.isEmpty()){
            throw new ValidationException("본문이 없습니다.");
        }
        if(this.postType.isNeedSeries() && this.series == null){
            throw new ValidationException("시리즈가 필요한 포스트입니다 !");
        }
        return true;
    }

}
