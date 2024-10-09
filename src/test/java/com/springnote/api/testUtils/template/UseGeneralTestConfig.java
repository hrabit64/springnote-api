package com.springnote.api.testUtils.template;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;

/**
 * 일반적인 테스트 설정을 사용하는 테스트 클래스에 붙이는 어노테이션
 */
@TestPropertySource(locations = "classpath:application-test.properties")
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public @interface UseGeneralTestConfig {
}
