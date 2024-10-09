package com.springnote.api.tests.integration;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.testUtils.auth.TestFirebaseToken;
import com.springnote.api.testUtils.auth.TestTokenUtils;
import com.springnote.api.testUtils.captcha.TestCaptchaToken;
import com.springnote.api.testUtils.template.IGTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.HttpMethod.POST;

public class AuthApiTest extends IGTestTemplate {

    @DisplayName("POST | /api/v1/auth | 유저 등록")
    @Nested
    class apiV1AuthPostUserTest {

        @DisplayName("유저 등록 성공")
        @DataSet(value = "datasets/ig/user/empty-user.yaml")
        @ExpectedDataSet(value = "datasets/ig/user/registered-user.yaml")
        @Test
        void success() {
            // given
            var authHeader = TestTokenUtils.createTokenHeader(TestFirebaseToken.USER_TOKEN);
            var url = createUrl(port, "/api/v1/auth?captchaToken=" + TestCaptchaToken.VALID_TOKEN.getToken());

            // when
            var result = restTemplate.exchange(url, POST, new HttpEntity<>(authHeader), String.class);

            // then
            assertEquals(200, result.getStatusCode().value());
        }

    }


}
