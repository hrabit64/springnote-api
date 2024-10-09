package com.springnote.api.dto.series.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.dto.series.service.SeriesCreateRequestServiceDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SeriesCreateRequestControllerDto {

    @NotEmpty(message = "제목은 필수 값 입니다.")
    @Size(min = 3, max = 100, message = "제목은 3자 이상, 100자 이하여야 합니다.")
    private String name;


    @Size(min = 1, max = 500, message = "설명은 1자 이상, 500자 이하여야 합니다.")
    private String description;

    @Pattern(regexp = "^(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)?$", message = "썸네일의 주소가 올바르지 않습니다.")
    private String thumbnail;

    public SeriesCreateRequestServiceDto toServiceDto() {
        return SeriesCreateRequestServiceDto.builder()
                .name(name)
                .description(description)
                .thumbnail(thumbnail)
                .build();
    }
}
