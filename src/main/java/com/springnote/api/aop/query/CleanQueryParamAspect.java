package com.springnote.api.aop.query;


import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.List;

import static com.springnote.api.utils.list.ListHelper.ignoreCaseContains;

@Deprecated
@Order(1)
@RequiredArgsConstructor
@Component
public class CleanQueryParamAspect {

    private final List<String> pageable = List.of("page", "size", "sort");

    @Before("within(com.springnote.api.web.controller..*)")
    public void beforeControllerMethod(JoinPoint joinPoint) {
        var signature = (MethodSignature) joinPoint.getSignature();
        var method = signature.getMethod();
        var args = joinPoint.getArgs();

        var parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].isAnnotationPresent(CleanQueryParam.class)) {
                if (!(args[i] instanceof MultiValueMap)) {
                    throw new IllegalArgumentException("Argument must be an instance of MultiValueMap");
                }
                @SuppressWarnings("unchecked")
                var queryParams = (MultiValueMap<String, String>) args[i];
                var keys = queryParams.keySet();

                // 삭제할 키를 임시 리스트에 모아둠
                List<String> keysToRemove = new ArrayList<>();

                // 키 순회하며 삭제할 키 수집
                for (String key : keys) {
                    if (ignoreCaseContains(pageable, key)) {
                        keysToRemove.add(key);
                    }
                }

                // 수집된 키 삭제
                keysToRemove.forEach(queryParams::remove);
            }
        }
    }
}
