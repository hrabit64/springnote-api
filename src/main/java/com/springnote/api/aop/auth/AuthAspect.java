package com.springnote.api.aop.auth;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.springnote.api.utils.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


// TODO : 인증 로직 구현
@Slf4j
@Order(2)
@Aspect
@RequiredArgsConstructor
@Component
public class AuthAspect {

    private final UserContext userContext;

    @Before("@annotation(enableAuth)")
    public void beforeRbacCheck(JoinPoint joinPoint, EnableAuth enableAuth) {
    }

    @Around("@annotation(enableAuth)")
    public Object aroundRbacCheck(ProceedingJoinPoint joinPoint, EnableAuth enableAuth) throws Throwable {
        // 헤더 값 검증 이후 원래 메소드 실행
        return joinPoint.proceed();
    }
}