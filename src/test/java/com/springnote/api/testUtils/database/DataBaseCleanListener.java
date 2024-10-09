//package com.springnote.api.utils.database;
//
//import jakarta.persistence.EntityManager;
//import org.jetbrains.annotations.NotNull;
//import org.junit.jupiter.api.Order;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.test.context.TestContext;
//import org.springframework.test.context.support.AbstractTestExecutionListener;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.nio.charset.StandardCharsets;
//
//@Order(1)
//public class DataBaseCleanListener extends AbstractTestExecutionListener {
//    @Override
//    public void beforeTestExecution(final @NotNull TestContext testContext) throws IOException {
//        final var em = findEntityManager(testContext);
//        executeSqlFromFile("sql/clean.sql", em);
//        executeSqlFromFile("sql/reset.sql", em);
//
//    }
//
//    private EntityManager findEntityManager(final TestContext testContext) {
//        return testContext.getApplicationContext()
//                .getBean(EntityManager.class);
//    }
//
//    private void executeSqlFromFile(String filePath, EntityManager em) throws IOException {
//        // Load SQL file from classpath
//        var resource = new ClassPathResource(filePath);
//
//        try (BufferedReader reader = new BufferedReader(
//                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {
//            var sqlBuilder = new StringBuilder();
//            String line;
//
//            // Read the SQL file line by line
//            while ((line = reader.readLine()) != null) {
//                sqlBuilder.append(line).append("\n");
//            }
//
//            // Split the SQL content by semicolon to get individual statements
//            String[] sqlStatements = sqlBuilder.toString().split(";\n");
//
//            // Execute each SQL statement
//            for (String statement : sqlStatements) {
//                String trimmedStatement = statement.trim();
//                if (!trimmedStatement.isEmpty()) {
//                    em.createNativeQuery(trimmedStatement).executeUpdate();
//                }
//            }
//        }
//    }
//}
