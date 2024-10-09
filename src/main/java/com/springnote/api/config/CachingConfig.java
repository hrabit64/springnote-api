package com.springnote.api.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Profile;

@Profile("!test")
@EnableCaching
public class CachingConfig {
}
