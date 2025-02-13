package com.springnote.api.filter.logging;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.springnote.api.filter.logging.loggers.DeactivateLogger;
import com.springnote.api.filter.logging.loggers.RegisterLogger;
import com.springnote.api.filter.logging.loggers.ReqResLogger;
import com.springnote.api.utils.context.RequestContext;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.json.JsonUtil;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
public class RequestResponseLoggingFilter implements Filter {

    private final RequestContext requestContext;
    private final JsonUtil jsonUtil;
    private final UserContext userContext;

    // API 경로와 HTTP 메서드를 매핑하여 로깅 대상 관리
    private static final Map<String, Set<ReqResLoggingConfig>> LOGGING_TARGETS = Map.of(
            "/api/v1/auth", Set.of(new ReqResLoggingConfig("POST", new RegisterLogger()),
                    new ReqResLoggingConfig("DELETE", new DeactivateLogger()))
    );

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        var req = (HttpServletRequest) request;
        var startRequest = LocalDateTime.now();

        chain.doFilter(request, response);

        var res = (HttpServletResponse) response;
        doLogging(req, res, startRequest);
    }

    private void doLogging(HttpServletRequest req, HttpServletResponse res, LocalDateTime start) {
        var end = LocalDateTime.now();
        var method = req.getMethod();
        var path = req.getRequestURI();
        var queryString = req.getQueryString();
        var fullPath = (queryString != null) ? path + "?" + queryString : path;

        loggingCommon(req, res, start, end, fullPath);

        // 로깅 대상 API에 해당하는 경우 로그 출력
        LOGGING_TARGETS.forEach((apiPath, configs) -> {
            for (var conf : configs) {
                if (conf.getMethod().equalsIgnoreCase(method) && path.startsWith(apiPath)) {
                    conf.logger.doLogging(
                            req,
                            res,
                            start,
                            end,
                            userContext,
                            requestContext.getId()
                    );
                }
            }
        });
    }

    private void loggingCommon(HttpServletRequest req, HttpServletResponse res, LocalDateTime start, LocalDateTime end, String fullPath) {
        var loggingBody = LoggingBody.builder()
                .id(requestContext.getId())
                .method(req.getMethod())
                .status(res.getStatus())
                .path(req.getRequestURI())
                .fullPath(fullPath)
                .remoteAddr(requestContext.getIp())
                .userAgent(req.getHeader("User-Agent"))
                .userUid(userContext.getUid())
                .contentType(req.getContentType())
                .requestTimestamp(start)
                .responseTimestamp(end)
                .processingTime(Duration.between(start, end).toMillis())
                .build();

        log.info("ReqRes:{}", jsonUtil.toJson(loggingBody));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class LoggingBody {
        private String id;
        private String method;
        private int status;
        private String path;
        private String fullPath;
        private String remoteAddr;
        private String userAgent;
        private String userUid;
        private String contentType;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime requestTimestamp;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime responseTimestamp;

        // 밀리초 단위 처리 시간
        private long processingTime;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class ReqResLoggingConfig {
        private String method;
        private ReqResLogger logger;
    }
}
