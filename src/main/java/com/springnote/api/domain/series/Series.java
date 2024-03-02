package com.springnote.api.domain.series;

import jakarta.persistence.*;
import lombok.*;


/**
 * 시리즈를 나타내는 엔티티 클래스입니다.
 * 
 * @auther 황준서 (hzser123@gmail.com)
 * @since 1.0.0
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "SERIES")
public class Series {

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
     * 
     * @auther 황준서 (hzser123@gmail.com)
     * @since 1.0.0
     */
    public void update(Series series){
        this.name = series.getName();
        this.description = series.getDescription();
        this.thumbnail = series.getThumbnail();
    }
}