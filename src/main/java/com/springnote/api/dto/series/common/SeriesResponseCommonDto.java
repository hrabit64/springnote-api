package com.springnote.api.dto.series.common;

import com.springnote.api.domain.series.Series;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

@Relation(collectionRelation = "series")
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesResponseCommonDto {
    private Long id;
    private String name;
    private String description;
    private String thumbnail;

    public SeriesResponseCommonDto(Series series) {
        this.id = series.getId();
        this.name = series.getName();
        this.description = series.getDescription();
        this.thumbnail = series.getThumbnail();
    }
}
