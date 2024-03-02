package com.springnote.api.utils.testDataBuilder;


import com.springnote.api.domain.series.Series;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;



@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TestSeriesBuilder {

    private Long id;

    @Builder.Default
    private String name = "테스트 시리즈";
    
    @Builder.Default
    private String description = "자동 생성된 테스트 시리즈입니다.";
    
    @Builder.Default
    private String thumbnail = "https://thisisfakethumbnailurl.hosisgodgame/image/828";
    

    public Series toEntity() {
        return Series.builder()
                .id(id)
                .name(name)
                .description(description)
                .thumbnail(thumbnail)
                .build();
    }
}
