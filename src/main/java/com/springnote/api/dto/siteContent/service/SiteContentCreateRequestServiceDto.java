package com.springnote.api.dto.siteContent.service;

import com.springnote.api.domain.siteContent.SiteContent;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SiteContentCreateRequestServiceDto {
    private String key;
    private String value;

    public SiteContent toEntity() {
        return SiteContent.builder()
                .key(key)
                .value(value)
                .build();
    }
}
