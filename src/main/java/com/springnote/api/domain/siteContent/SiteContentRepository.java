package com.springnote.api.domain.siteContent;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SiteContentRepository extends JpaRepository<SiteContent, String> {
    boolean existsByKey(String key);
}
