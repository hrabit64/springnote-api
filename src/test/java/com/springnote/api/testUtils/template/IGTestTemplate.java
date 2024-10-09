package com.springnote.api.testUtils.template;

import com.springnote.api.config.TestQueryDslConfig;
import com.springnote.api.testUtils.auth.TestFirebaseAuthManager;
import com.springnote.api.testUtils.captcha.TestCaptchaManager;
import com.springnote.api.testUtils.database.UseTestContainer;
import com.springnote.api.utils.type.TypeParser;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.AuditingHandler;

import java.time.LocalDateTime;
import java.util.Optional;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import({TestQueryDslConfig.class, TypeParser.class, TestFirebaseAuthManager.class, TestCaptchaManager.class})
@UseGeneralTestConfig
@UseTestContainer
public class IGTestTemplate {

    @LocalServerPort
    public int port;

    @Autowired
    public TestRestTemplate restTemplate;

    @SpyBean
    private AuditingHandler auditingHandler;

    @BeforeEach
    public void setup() {
        auditingHandler.setDateTimeProvider(() -> Optional.of(LocalDateTime.of(2002, 8, 28, 0, 0, 0, 0)));

    }

    public static String getBaseUrl(int port) {
        return "http://localhost:" + port;
    }

    public static String createUrl(int port, String path) {
        return getBaseUrl(port) + path;
    }


}
