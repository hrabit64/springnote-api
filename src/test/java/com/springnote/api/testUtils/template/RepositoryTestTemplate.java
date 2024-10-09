package com.springnote.api.testUtils.template;

import com.springnote.api.config.TestQueryDslConfig;
import com.springnote.api.testUtils.database.UseTestContainer;
import com.springnote.api.utils.type.TypeParser;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.auditing.AuditingHandler;

import java.time.LocalDateTime;
import java.util.Optional;


@Import({TestQueryDslConfig.class, TypeParser.class})
@DataJpaTest(showSql = false)
@UseGeneralTestConfig
@UseTestContainer
public class RepositoryTestTemplate {


    @SpyBean
    private AuditingHandler auditingHandler;

    @BeforeEach
    public void setup() {
        auditingHandler.setDateTimeProvider(() -> Optional.of(LocalDateTime.of(2002, 8, 28, 0, 0, 0, 0)));

    }

    @Autowired
    private EntityManager entityManager;

    @BeforeEach
    void resetAutoIncrementId() {

        var tableNames = entityManager.createNativeQuery("SHOW TABLES")
                .getResultList();

        var tableNamesString = tableNames.stream()
                .map(row -> (String) row)
                .toArray(String[]::new);

        for (var tableName : tableNamesString) {
            entityManager.createNativeQuery("ALTER TABLE `" + tableName + "` AUTO_INCREMENT = 1;")
                    .executeUpdate();
        }
    }


}
