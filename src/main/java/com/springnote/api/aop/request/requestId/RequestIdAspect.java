package com.springnote.api.aop.request.requestId;

import java.util.UUID;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.springnote.api.utils.context.RequestIdContext;

import lombok.RequiredArgsConstructor;

/**
 * Rest Controller class에 대하여 Request Id (UUID) 를 생성하는 Aspect 클래스
 * 
 * @author 황준서('hzser123@gmail.com')
 * @since 1.0.0
 */
@Order(1)
@Aspect
@RequiredArgsConstructor
@Component
public class RequestIdAspect {

    // request id 가 저장될 context 클래스
    private final RequestIdContext requestIdContext;

    @Pointcut("within(org.springframework.web.bind.annotation.RestController)")
    public void targetControllerMethods() {
    }

    @Before("targetControllerMethods()")
    public void beforeControllerMethod() {
        requestIdContext.setId(UUID.randomUUID().toString());
    }

}