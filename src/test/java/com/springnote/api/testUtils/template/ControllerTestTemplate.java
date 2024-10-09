package com.springnote.api.testUtils.template;

import com.springnote.api.config.JacksonConfig;
import com.springnote.api.config.TestAspectConfig;
import com.springnote.api.config.TestEnableAuthAspect;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.json.JsonUtil;
import com.springnote.api.utils.time.TimeHelper;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;

import java.time.format.DateTimeFormatter;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDate;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static org.mockito.Mockito.doReturn;

@AutoConfigureRestDocs
@Import({TestEnableAuthAspect.class, TestAspectConfig.class, JacksonConfig.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseGeneralTestConfig
public class ControllerTestTemplate {

    @MockBean
    public TimeHelper timeHelper;

    @SpyBean
    public UserContext userContext;

    public JsonUtil jsonUtil = new JsonUtil();

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @BeforeEach
    public void setUp() {
        doReturn(testLocalDateTime()).when(timeHelper).nowTime();
        doReturn(testLocalDate()).when(timeHelper).nowDate();
    }
}
