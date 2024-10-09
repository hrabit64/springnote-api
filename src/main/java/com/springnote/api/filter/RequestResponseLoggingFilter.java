package com.springnote.api.filter;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springnote.api.utils.context.RequestContext;
import com.springnote.api.utils.json.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
public class RequestResponseLoggingFilter implements Filter {

    private final RequestContext requestContext;
    private final JsonUtil jsonUtil;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var req = (HttpServletRequest) request;

        var startRequest = LocalDateTime.now();

        try {
            loggingRequest(req, startRequest);
        } catch (IOException e) {
            log.error("Error while logging request", e);
        }

        chain.doFilter(request, response);

        var res = (HttpServletResponse) response;

        try {
            loggingResponse(res, startRequest);
        } catch (Exception e) {
            log.error("Error while logging response", e);
        }
    }

    private void loggingRequest(HttpServletRequest req, LocalDateTime start) throws IOException {
        var path = req.getRequestURI();
        var id = requestContext.getId();
        var remoteAddr = requestContext.getIp();
        var requestBody = req.getReader().lines().reduce("", (accumulator, actual) -> accumulator + actual);
        var method = req.getMethod();
        var contentType = req.getContentType();

        var requestLoggingBody = RequestLoggingBody.builder()
                .id(id)
                .path(path)
                .remoteAddr(remoteAddr)
                .requestBody(requestBody)
                .method(method)
                .timestamp(start)
                .contentType(contentType)
                .build();

        log.info("New Request :{}", jsonUtil.toJson(requestLoggingBody));
    }

    private void loggingResponse(HttpServletResponse res, LocalDateTime start) {
        var id = requestContext.getId();
        var path = res.getContentType();
        var remoteAddr = requestContext.getIp();
        var responseBody = res.getContentType();
        var method = res.getContentType();
        var status = res.getStatus();
        var contentType = res.getContentType();
        var endTimestamp = LocalDateTime.now();

        var responseLoggingBody = ResponseLoggingBody.builder()
                .id(id)
                .path(path)
                .remoteAddr(remoteAddr)
                .responseBody(responseBody)
                .method(method)
                .status(status)
                .contentType(contentType)
                .startTimestamp(start)
                .endTimestamp(endTimestamp)
                .build();

        log.info("New Response :{}", jsonUtil.toJson(responseLoggingBody));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class RequestLoggingBody {
        private String id;
        private String path;
        private String remoteAddr;
        private String requestBody;
        private String method;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime timestamp;
        private String contentType;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class ResponseLoggingBody {
        private String id;
        private String path;
        private String remoteAddr;
        private String responseBody;
        private String method;
        private int status;
        private String contentType;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime startTimestamp;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime endTimestamp;

    }
}
