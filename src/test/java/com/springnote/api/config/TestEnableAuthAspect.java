package com.springnote.api.config;

import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.exception.auth.AuthErrorCode;
import com.springnote.api.utils.exception.auth.AuthException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.boot.test.context.TestConfiguration;


// 해당 컴포넌트로 Controller Layer를 테스트 할 때 사용함. 이때 컨트롤러에 올바른 인증 레벨을 걸었는지만 검증함.
@Slf4j
@Aspect
@RequiredArgsConstructor
@TestConfiguration
public class TestEnableAuthAspect {

    // 유저 컨텍스트를 무킹하여 해당 컨텍스트의 유저가 요청을 한 상황으로 가정함.
    private final UserContext userContext;

    @Before("@annotation(enableAuthentication)")
    public void beforeAuthCheck(JoinPoint joinPoint, EnableAuthentication enableAuthentication) {
        log.info("this controller auth level is {}", enableAuthentication.value());
        if (!isPassedAuthLevel(enableAuthentication.value())) {
            log.debug("필요한 인증 레벨 {}", enableAuthentication.value());
            throw new AuthException(AuthErrorCode.FORBIDDEN_ROLE, "인증 레벨이 부족합니다.");
        }
    }

    private boolean isPassedAuthLevel(AuthLevel authLevel) {
        return switch (authLevel) {
            case ADMIN -> userContext.isAdmin();
            case USER -> userContext.isAuthed();
            case NONE -> true;
        };
    }

}
