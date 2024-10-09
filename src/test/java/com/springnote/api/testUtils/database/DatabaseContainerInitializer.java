package com.springnote.api.testUtils.database;

import com.github.dockerjava.api.model.RestartPolicy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.DockerClientFactory;
import org.testcontainers.containers.MariaDBContainer;

@Slf4j
public class DatabaseContainerInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    private static final String TESTCONTAINERS_DISABLE_PROPERTY = "testcontainers.disable";

    @Getter
    private static MariaDBContainer<?> database = new MariaDBContainer<>("mariadb:11.2.3")
            .withUsername("admin")
            .withPassword("admin")
            .withDatabaseName("test")
            .withReuse(true)
            .withCreateContainerCmdModifier(cmd -> cmd.getHostConfig().withRestartPolicy(RestartPolicy.alwaysRestart()))
            .withLabel("group", "test-db")
            .withInitScript("sql/init.sql");

    @Override
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

        database.start();

        log.info("Testcontainers started. JDBC URL: {}", database.getJdbcUrl());

        // 컨테이너 이름을 변경하기 위해 Docker API 사용
//        renameContainer();

        TestPropertyValues.of(
                "spring.datasource.url=" + database.getJdbcUrl(),
                "spring.datasource.password=" + database.getPassword(),
                "spring.datasource.username=" + database.getUsername(),
                "spring.test.database.replace=none"
        ).applyTo(configurableApplicationContext.getEnvironment());
    }

    // 실행 이후 gradle 을 통해 DB 컨테이너를 종료하기 용이하기 위해 사용함
    private static void renameContainer() {
        try {
            var dockerClient = DockerClientFactory.instance().client();
            var containerId = database.getContainerId();
            var newContainerName = "test-mariadb";

            dockerClient.renameContainerCmd(containerId).withName(newContainerName).exec();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isTestcontainersDisabled(ConfigurableApplicationContext configurableApplicationContext) {
        var testcontainersDisabled = configurableApplicationContext.getEnvironment().getProperty(TESTCONTAINERS_DISABLE_PROPERTY, Boolean.class);
        return Boolean.TRUE.equals(testcontainersDisabled);
    }

}
