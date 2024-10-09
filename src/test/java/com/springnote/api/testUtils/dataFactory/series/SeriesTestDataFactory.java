package com.springnote.api.testUtils.dataFactory.series;

import com.springnote.api.domain.series.Series;

public class SeriesTestDataFactory {
    public static Series createSeries(Long id) {
        return Series.builder()
                .id(id)
                .name("series")
                .description("description")
                .build();
    }

    public static Series createSeries() {
        return Series.builder()
                .id(1L)
                .name("series")
                .description("description")
                .build();
    }

    public static Series createSeries(String name) {
        return Series.builder()
                .id(1L)
                .name(name)
                .description("description")
                .build();
    }

    public static Series createSeries(String name, String description) {
        return Series.builder()
                .id(1L)
                .name(name)
                .description(description)
                .build();
    }
}
