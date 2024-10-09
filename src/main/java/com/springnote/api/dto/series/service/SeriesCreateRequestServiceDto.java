package com.springnote.api.dto.series.service;

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
public class SeriesCreateRequestServiceDto {
    private String name;
    private String description;
    private String thumbnail;

    public Series toEntity(){
        return Series.builder()
                .name(name)
                .description(description)
                .thumbnail(thumbnail)
                .build();
    }
}
