package com.springnote.api.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.util.List;

@Getter
@Configuration
public class ImageConfig {
    @Value("${springnote.image.base-path}")
    private String basePathName;

    @Value("${springnote.image.base-format}")
    private String baseFormat;

    @Value("${springnote.image.max-width}")
    private int maxWidth;

    @Value("#{'${springnote.image.allowed-image-format}'.split(',')}")
    private List<String> allowedFormat;

    public Path getBasePath() {
        return Path.of(basePathName);
    }
}
