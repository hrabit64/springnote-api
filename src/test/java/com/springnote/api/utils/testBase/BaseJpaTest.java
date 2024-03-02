package com.springnote.api.utils.testBase;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.springnote.api.config.TestQueryDslConfig;
import com.springnote.api.utils.TypeParser;

import jakarta.validation.constraints.NotNull;


//ref:https://alicanbalik.medium.com/integration-test-with-testcontainers-in-spring-boot-e6d0895c11d0
@Import({ TestQueryDslConfig.class, TypeParser.class})
@DataJpaTest
@SqlGroup({
        @Sql(scripts = "classpath:truncate.sql",executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
})
@Transactional(propagation = Propagation.NOT_SUPPORTED)
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = BaseJpaTest.DataSourceInitializer.class)
public abstract class BaseJpaTest {

    
        @Container
        private static MariaDBContainer<?> database = new MariaDBContainer<>("mariadb:11.2.2")
                        .withDatabaseName("test")
                        .withUsername("testuser")
                        .withPassword("testuser")
                        .withInitScript("schema.sql")
                        .withCommand("--character-set-server=utf8mb4 --collation-server=utf8mb4_unicode_ci");

    public static class DataSourceInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        @Override
        public void initialize(@NotNull ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(
                    applicationContext,
                    "spring.test.database.replace=none", // Tells Spring Boot not to start in-memory db for tests.
                    "spring.datasource.url=" + database.getJdbcUrl(),
                    "spring.datasource.username=" + database.getUsername(),
                    "spring.datasource.password=" + database.getPassword()
            );
        }
    }

    @SpyBean
    private AuditingHandler auditingHandler;

    @BeforeEach
    public void setup() {
        auditingHandler.setDateTimeProvider(() -> Optional.of(LocalDateTime.of(2002,8,28,0,0,0,0)));
    
    }
}
