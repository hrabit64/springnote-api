package com.springnote.api.testUtils.dataFactory.series;

import com.springnote.api.dto.series.common.SeriesResponseCommonDto;

public class SeriesDtoTestDataFactory {
    public static SeriesResponseCommonDto createSeriesResponseCommonDto() {
        return SeriesResponseCommonDto.builder()
                .id(1L)
                .name("test")
                .description("test")
                .thumbnail("https://springnote.blog")
                .build();
    }
}
