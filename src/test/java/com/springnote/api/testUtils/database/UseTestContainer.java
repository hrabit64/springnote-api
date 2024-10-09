package com.springnote.api.testUtils.database;


import com.github.database.rider.core.api.configuration.DBUnit;
import com.github.database.rider.spring.api.DBRider;
import com.springnote.api.testUtils.logging.EnableSqlLogging;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlGroup;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

@Transactional(propagation = Propagation.NOT_SUPPORTED)
@SqlGroup({
        @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_CLASS, scripts = "classpath:sql/clean.sql"),
})
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@DBRider
@DBUnit(dataTypeFactoryClass = MariaDBDataTypeFactory.class, allowEmptyFields = true, cacheConnection = false)
@EnableSqlLogging
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = DatabaseContainerInitializer.class)
public @interface UseTestContainer {
}
