package com.springnote.api.config;

import com.springnote.api.service.ConfigService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@RequiredArgsConstructor
@Configuration
public class CommentConfig {

    private final ConfigService configService;

    private final String confKeyCommentWriteEnable = "comment.write-enable";
    private final String confKeyCommentViewEnable = "comment.view-enable";

    public boolean isWriteEnable() {
        return Boolean.parseBoolean(configService.getConfig(confKeyCommentWriteEnable));
    }

    public boolean isViewEnable() {
        return Boolean.parseBoolean(configService.getConfig(confKeyCommentViewEnable));
    }

}
