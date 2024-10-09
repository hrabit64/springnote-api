package com.springnote.api.dto.series.common;

import com.springnote.api.domain.series.Series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeriesSimpleResponseDto {
    private Long id;
    private String name;

    public SeriesSimpleResponseDto(Series series) {
        this.id = series.getId();
        this.name = series.getName();
    }
}
