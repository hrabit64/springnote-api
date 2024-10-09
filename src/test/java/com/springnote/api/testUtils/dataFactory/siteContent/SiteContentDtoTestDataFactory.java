package com.springnote.api.testUtils.dataFactory.siteContent;

import com.springnote.api.dto.siteContent.common.SiteContentResponseCommonDto;

public class SiteContentDtoTestDataFactory {
    public static SiteContentResponseCommonDto createSiteContentResponseCommonDto() {
        return SiteContentResponseCommonDto.builder()
                .key("test")
                .content("test content")
                .build();
    }

}
