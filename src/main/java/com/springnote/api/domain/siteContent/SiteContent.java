package com.springnote.api.domain.siteContent;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@EqualsAndHashCode
@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "SITE_CONTENT")
public class SiteContent {

    @Id
    @Column(name = "SITE_CONTENT_KEY", length = 300)
    private String key;

    @Column(name = "SITE_CONTENT_VALUE", length = 30000)
    private String value;

}
