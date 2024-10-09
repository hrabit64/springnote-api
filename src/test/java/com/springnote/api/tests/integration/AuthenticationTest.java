package com.springnote.api.tests.integration;

import com.github.database.rider.core.api.dataset.DataSet;
import com.springnote.api.testUtils.auth.TestFirebaseToken;
import com.springnote.api.testUtils.template.IGTestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;

import java.util.Objects;
import java.util.stream.Stream;

import static com.springnote.api.testUtils.auth.TestTokenUtils.createTokenHeader;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AuthenticationTest extends IGTestTemplate {

    @DisplayName("Admin 권한 테스트")
    @Nested
    class AdminTest {

        @DataSet(value = "datasets/ig/user/base-user.yaml")
        @DisplayName("어드민이 Admin 권한 페이지에 접근시 성공한다.")
        @Test
        void testAdminAccessAdminPage() {
            // given
            var authHeader = createTokenHeader(TestFirebaseToken.ADMIN_TOKEN);
            var requestEntity = new HttpEntity<>(authHeader);

            // when
            var response = restTemplate.exchange(
                    "/admin",
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            // then
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertTrue(Objects.requireNonNull(response.getBody()).contains("Hello, SpringNote Admin!"));
        }

        private static Stream<Arguments> provideInvalidRoleTokens() {
            return Stream.of(
                    Arguments.of(TestFirebaseToken.NOT_VALID, "미인증 사용자"),
                    Arguments.of(TestFirebaseToken.USER_TOKEN, "일반 사용자"),
                    Arguments.of(TestFirebaseToken.DISABLED_USER_TOKEN, "비활성화된 사용자"),
                    Arguments.of(TestFirebaseToken.DISABLED_ADMIN_TOKEN, "비활성화된 Admin")
            );
        }

        @DataSet(value = "datasets/ig/user/base-user.yaml")
        @DisplayName("올바르지 않은 권한의 사용자가 Admin 권한 페이지에 접근시 실패한다.")
        @MethodSource("provideInvalidRoleTokens")
        @ParameterizedTest(name = "{index} : {1}")
        void testNonAdminAccessAdminPage(TestFirebaseToken token, String role) {
            // given
            var authHeader = createTokenHeader(token);
            var requestEntity = new HttpEntity<>(authHeader);

            // when
            var response = restTemplate.exchange(
                    "/admin",
                    HttpMethod.GET,
                    requestEntity,
                    String.class
            );

            // then
            assertTrue(response.getStatusCode().is4xxClientError());
        }
    }

    @DisplayName("User 권한 테스트")
    @Nested
    class UserTest {

        private static Stream<Arguments> provideValidRoleTokens() {
            return Stream.of(
                    Arguments.of(TestFirebaseToken.USER_TOKEN, "일반 사용자"),
                    Arguments.of(TestFirebaseToken.ADMIN_TOKEN, "Admin")
            );

        }

        @DataSet(value = "datasets/ig/user/base-user.yaml")
        @DisplayName("올바른 권한을 가진 User가 User 권한 페이지에 접근시 성공한다.")
        @MethodSource("provideValidRoleTokens")
        @ParameterizedTest(name = "{index} : {1}")
        void testUserAccessUserPage(TestFirebaseToken token, String role) {
            // given
            var authHeader = createTokenHeader(token);
            var requestEntity = new HttpEntity<>(authHeader);

            // when
            var response = restTemplate.exchange(
                    "/user",
                    HttpMethod.GET,
                    requestEntity,
                    String.class);

            // then
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertTrue(Objects.requireNonNull(response.getBody()).contains("Hello, SpringNote User!"));

        }

        private static Stream<Arguments> provideInvalidRoleTokens() {
            return Stream.of(
                    Arguments.of(TestFirebaseToken.NOT_VALID, "미인증 사용자"),
                    Arguments.of(TestFirebaseToken.DISABLED_USER_TOKEN, "비활성화된 사용자"),
                    Arguments.of(TestFirebaseToken.DISABLED_ADMIN_TOKEN, "비활성화된 Admin")
            );
        }

        @DataSet(value = "datasets/ig/user/base-user.yaml")
        @DisplayName("올바르지 않은 권한의 사용자가 User 권한 페이지에 접근시 실패한다.")
        @MethodSource("provideInvalidRoleTokens")
        @ParameterizedTest(name = "{index} : {1}")
        void testNonUserAccessUserPage(TestFirebaseToken token, String role) {
            // given
            var authHeader = createTokenHeader(token);
            var requestEntity = new HttpEntity<>(authHeader);

            // when
            var response = restTemplate.exchange(
                    "/user",
                    HttpMethod.GET,
                    requestEntity,
                    String.class);

            // then
            assertTrue(response.getStatusCode().is4xxClientError());
        }

    }

    @DisplayName("미인증 사용자 테스트")
    @Nested
    class NotAuthenticatedTest {

        private static Stream<Arguments> provideValidRoleTokens() {
            return Stream.of(

                    Arguments.of(TestFirebaseToken.USER_TOKEN, "일반 사용자"),
                    Arguments.of(TestFirebaseToken.ADMIN_TOKEN, "Admin"),
                    Arguments.of(TestFirebaseToken.NOT_VALID, "미인증 사용자"),
                    Arguments.of(TestFirebaseToken.DISABLED_USER_TOKEN, "비활성화된 사용자"),
                    Arguments.of(TestFirebaseToken.DISABLED_ADMIN_TOKEN, "비활성화된 Admin")
            );
        }

        @DataSet(value = "datasets/ig/user/base-user.yaml")
        @DisplayName("모든 사용자가 None 권한 페이지에 접근시 성공한다.")
        @MethodSource("provideValidRoleTokens")
        @ParameterizedTest(name = "{index} : {1}")
        void testAllAccessNonePage(TestFirebaseToken token, String role) {
            // given
            var authHeader = createTokenHeader(token);
            var requestEntity = new HttpEntity<>(authHeader);

            // when
            var response = restTemplate.exchange(
                    "/none",
                    HttpMethod.GET,
                    requestEntity,
                    String.class);

            // then
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertTrue(Objects.requireNonNull(response.getBody()).contains("Hello, SpringNote!"));
        }

    }
}