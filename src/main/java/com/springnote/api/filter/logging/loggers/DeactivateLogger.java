package com.springnote.api.filter.logging.loggers;

import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.json.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class DeactivateLogger extends ReqResLogger {

    private final JsonUtil jsonUtil = new JsonUtil();

    @Override
    public void doLogging(HttpServletRequest req, HttpServletResponse res, LocalDateTime startTime, LocalDateTime endTime, UserContext userContext, String reqId) {
        var deactivateLog = DeactivateLog.builder()
                .id(reqId)
                .uid(userContext.getFbUid())
                .email(userContext.getFbEmail())
                .deactivateTime(endTime)
                .ip(req.getRemoteAddr())
                .result(res.getStatus() == 200 ? "true" : "false")
                .build();

        log.info("Deactivate:{}", jsonUtil.toJson(deactivateLog));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class DeactivateLog {
        private String id;
        private String uid;
        private String email;
        private LocalDateTime deactivateTime;
        private String ip;
        private String result;

    }
}
