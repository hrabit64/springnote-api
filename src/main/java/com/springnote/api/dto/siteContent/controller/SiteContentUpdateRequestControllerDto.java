package com.springnote.api.dto.siteContent.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.dto.siteContent.service.SiteContentUpdateRequestServiceDto;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SiteContentUpdateRequestControllerDto {
    @Size(min = 1, max = 30000, message = "본문은 3자 이상, 30000자 이하여야 합니다.")
    @NotEmpty(message = "본문을 입력해주세요.")
    private String content;

    public SiteContentUpdateRequestServiceDto toServiceDto(String key) {
        return SiteContentUpdateRequestServiceDto.builder()
                .key(key)
                .value(content)
                .build();
    }
}
