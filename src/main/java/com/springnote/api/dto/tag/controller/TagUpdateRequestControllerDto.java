package com.springnote.api.dto.tag.controller;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import com.springnote.api.dto.tag.service.TagUpdateRequestServiceDto;

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
public class TagUpdateRequestControllerDto {

    @NotEmpty(message = "태그 이름은 필수입니다.")
    @Size(min = 2, max = 100, message = "태그 이름은 2자 이상, 100자 이하여야 합니다.")
    private String name;

    public TagUpdateRequestServiceDto toServiceDto(Long id) {
        return TagUpdateRequestServiceDto.builder()
                .id(id)
                .name(name)
                .build();
    }

}
