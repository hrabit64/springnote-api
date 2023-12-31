package com.springnote.api.domain.jpa.series;

import com.springnote.api.domain.elasticsearch.seriesIndex.SeriesIndex;
import jakarta.persistence.*;
import lombok.*;


@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "series")
public class Series {

    @Id
    @Column(name = "series_pk", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "series_title", nullable = false, unique = true, length = 50)
    private String title;

    @Column(name = "series_description", nullable = false, length = 50)
    private String description;

    public void update(Series series){
        this.title = series.getTitle();
        this.description = series.getDescription();
    }

    public SeriesIndex toIndex(){
        return SeriesIndex.builder()
                .id(id)
                .title(title)
                .description(description)
                .build();
    }
}
