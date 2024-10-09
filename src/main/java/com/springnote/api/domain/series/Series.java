package com.springnote.api.domain.series;

import com.springnote.api.domain.BaseDateTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;


/**
 * 시리즈를 나타내는 엔티티 클래스입니다.
 *
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@AttributeOverrides({
        @AttributeOverride(name = "createdDate", column = @Column(name = "SERIES_CREATED_AT", updatable = false)),
        @AttributeOverride(name = "lastModifiedDate", column = @Column(name = "SERIES_LAST_MODIFIED_AT"))
})
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
@ToString
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity(name = "SERIES")
public class Series extends BaseDateTimeEntity {

    @Id
    @Column(name = "SERIES_PK", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SERIES_NM", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "SERIES_DESCRIPTION", nullable = false, length = 500)
    private String description;

    @Column(name = "SERIES_THUMB", nullable = false, length = 2000)
    private String thumbnail;

    /**
     * 주어진 시리즈 정보로 엔티티를 업데이트합니다.
     * name, description, thumbnail 을 업데이트합니다.
     *
     * @param series 업데이트할 시리즈 정보
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public void update(Series series) {
        this.description = series.getDescription();
        this.thumbnail = series.getThumbnail();
    }
}