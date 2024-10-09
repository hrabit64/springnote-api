package com.springnote.api.dto.siteContent.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.dto.siteContent.service.SiteContentCreateRequestServiceDto;
import com.springnote.api.utils.validation.string.CheckHasBlank;
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
public class SiteContentCreateRequestControllerDto {

    @CheckHasBlank(message = "키에 공백이 포함되어 있습니다.")
    @Size(min = 1, max = 300, message = "키는 1자 이상, 300자 이하여야 합니다.")
    @NotEmpty(message = "키를 입력해주세요.")
    private String key;

    @Size(min = 1, max = 30000, message = "본문은 30000자 이하여야 합니다.")
    @NotEmpty(message = "본문을 입력해주세요.")
    private String content;

    public SiteContentCreateRequestServiceDto toServiceDto() {
        return SiteContentCreateRequestServiceDto.builder()
                .key(key)
                .value(content)
                .build();
    }

}
