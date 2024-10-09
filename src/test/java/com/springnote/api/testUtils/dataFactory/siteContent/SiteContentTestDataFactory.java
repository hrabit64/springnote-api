package com.springnote.api.testUtils.dataFactory.siteContent;

import com.springnote.api.domain.siteContent.SiteContent;

public class SiteContentTestDataFactory {
    public static SiteContent createSiteContent() {
        return SiteContent.builder()
                .key("test")
                .value("test content")
                .build();
    }

    public static SiteContent createSiteContent(String key) {
        return SiteContent.builder()
                .key(key)
                .value("test content")
                .build();
    }
}
