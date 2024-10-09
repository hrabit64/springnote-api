package com.springnote.api.dto.siteContent.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.siteContent.SiteContent;
import lombok.*;

@EqualsAndHashCode
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SiteContentResponseCommonDto {
    private String key;
    private String content;

    public SiteContentResponseCommonDto(SiteContent siteContent) {
        this.key = siteContent.getKey();
        this.content = siteContent.getValue();
    }
}
