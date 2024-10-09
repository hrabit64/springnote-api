package com.springnote.api.dto.series.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.springnote.api.dto.series.service.SeriesUpdateRequestServiceDto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
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
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SeriesUpdateRequestControllerDto {
    
    @NotEmpty(message = "제목은 필수 값 입니다.")
    @Size(min = 3, max = 100, message = "제목은 3자 이상, 100자 이하여야 합니다.")
    private String name;

    @Size(min = 1, max = 500, message = "설명은은 1자 이상, 500자 이하여야 합니다.")
    private String description;

    @Pattern(regexp = "^(https?:\\/\\/)?(www\\.)?[-a-zA-Z0-9@:%._\\+~#=]{2,256}\\.[a-z]{2,6}\\b([-a-zA-Z0-9@:%_\\+.~#?&//=]*)?$", message = "썸네일의 주소가 올바르지 않습니다.")
    private String thumbnail;


    public SeriesUpdateRequestServiceDto toServiceDto(Long id) {
        return SeriesUpdateRequestServiceDto.builder()
                .id(id)
                .name(name)
                .description(description)
                .thumbnail(thumbnail)
                .build();
    }
}
