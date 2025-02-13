package com.springnote.api.filter.logging.loggers;

import com.springnote.api.dto.user.common.UserSimpleResponseCommonDto;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.json.JsonUtil;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Slf4j
public class RegisterLogger extends ReqResLogger {

    private final JsonUtil jsonUtil = new JsonUtil();


    @Override
    public void doLogging(HttpServletRequest req, HttpServletResponse res, LocalDateTime startTime, LocalDateTime endTime, UserContext userContext, String reqId) {
        var registerLog = RegisterLog.builder()
                .id(reqId)
                .uid(userContext.getFbUid())
                .email(userContext.getFbEmail())
                .registerTime(endTime)
                .ip(req.getRemoteAddr())
                .result(res.getStatus() == 200 ? "true" : "false")
                .build();


        log.info("Register:{}", jsonUtil.toJson(registerLog));
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    private static class RegisterLog {
        private String id;
        private String uid;
        private String email;
        private LocalDateTime registerTime;
        private String ip;
        private String result;

    }
}
