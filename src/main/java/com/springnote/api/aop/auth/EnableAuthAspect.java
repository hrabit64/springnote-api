package com.springnote.api.aop.auth;

import com.springnote.api.dto.user.common.UserResponseCommonDto;
import com.springnote.api.security.auth.AuthManager;
import com.springnote.api.security.auth.AuthUserInfo;
import com.springnote.api.security.auth.TokenHeaderUtils;
import com.springnote.api.service.UserService;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.exception.auth.AuthErrorCode;
import com.springnote.api.utils.exception.auth.AuthException;
import com.springnote.api.utils.exception.business.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Order(6)
@Aspect
@RequiredArgsConstructor
@Component
public class EnableAuthAspect {

    private final UserContext userContext;
    private final AuthManager authManager;
    private final UserService userService;

    @Before("@annotation(enableAuthentication)")
    public void beforeAuthCheck(JoinPoint joinPoint, EnableAuthentication enableAuthentication) {
        var request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        var authLevel = enableAuthentication.value();
        var tokenHeader = request.getHeader("Authorization");

        if (!validateTokenHeader(tokenHeader, authLevel)) return;

        var token = tokenHeader.substring(7);

        var tokenInfo = authManager.authenticate(token);


        if (!validateToken(tokenInfo, authLevel)) return;
        userContext.setFbInfo(tokenInfo.getUid(), tokenInfo.getEmail());

        if (!validateUser(tokenInfo, authLevel)) return;

        checkRole(authLevel);

    }


    @Around("@annotation(enableAuthentication)")
    public Object aroundAuthCheck(ProceedingJoinPoint joinPoint, EnableAuthentication enableAuthentication) throws Throwable {
        return joinPoint.proceed();
    }

    private void setNotAuthenticated(AuthLevel authLevel) {
        userContext.init();
        checkRole(authLevel);
    }

    private boolean validateUser(AuthUserInfo tokenInfo, AuthLevel authLevel) {
        UserResponseCommonDto user;
        try {
            user = userService.getUser(tokenInfo.getUid());
        } catch (BusinessException e) {
            return false;
        }
        if (!user.isEnabled()) {
            setNotAuthenticated(authLevel);
            return false;
        } else {
            userContext.init(user);
        }
        return true;
    }

    private boolean validateToken(AuthUserInfo tokenInfo, AuthLevel authLevel) {
        if (tokenInfo == null) {
            setNotAuthenticated(authLevel);
            return false;
        }
        return true;
    }

    private boolean validateTokenHeader(String tokenHeader, AuthLevel authLevel) {
        var token = TokenHeaderUtils.extractToken(tokenHeader);
        if (token == null) {
            setNotAuthenticated(authLevel);
            return false;
        }
        return true;
    }

    private void checkRole(AuthLevel authLevel) {
        if (authLevel == AuthLevel.ADMIN) {
            if (!userContext.isAdmin()) {
                throw new AuthException(AuthErrorCode.FORBIDDEN_ROLE, "권한이 없습니다.");
            }
        }

        if (authLevel == AuthLevel.USER) {
            if (!userContext.isAuthed()) {
                throw new AuthException(AuthErrorCode.FORBIDDEN_ROLE, "로그인이 필요합니다.");
            }
        }

    }
}