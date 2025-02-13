package com.springnote.api.filter.logging.loggers;

import com.springnote.api.dto.user.common.UserSimpleResponseCommonDto;
import com.springnote.api.utils.context.UserContext;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;

public abstract class ReqResLogger {
    public abstract void doLogging(HttpServletRequest req, HttpServletResponse res, LocalDateTime startTime, LocalDateTime endTime, UserContext userContext, String reqId);
}
