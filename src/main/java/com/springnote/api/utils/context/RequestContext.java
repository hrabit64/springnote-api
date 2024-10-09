package com.springnote.api.utils.context;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

/**
 * 각 Request 에 대한 고유 ID를 표시하기 위한 클래스
 *
 * @author 황준서(' hzser123 @ gmail.com ')
 * @since 1.0.0
 */
@Setter
@Getter
@Component
@Scope(value = "request", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RequestContext {

    // request id (UUID)
    private String id = null;

    private String ip = null;
}