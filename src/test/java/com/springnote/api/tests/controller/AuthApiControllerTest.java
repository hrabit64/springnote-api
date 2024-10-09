package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.config.AuthConfig;
import com.springnote.api.dto.user.service.UserCreateRequestServiceDto;
import com.springnote.api.security.auth.AuthManager;
import com.springnote.api.security.auth.AuthUserInfo;
import com.springnote.api.security.captcha.CaptchaManager;
import com.springnote.api.service.UserService;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.web.controller.AuthApiController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createUserContextReturns;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller Test - AuthApiController")
@WebMvcTest(AuthApiController.class)
public class AuthApiControllerTest extends ControllerTestTemplate {

    @Autowired
    private AuthApiController authApiController;

    @MockBean
    private UserService userService;

    @MockBean
    private UserContext userContext;

    @MockBean
    private AuthManager authManager;

    @MockBean
    private AuthConfig authConfig;

    @MockBean
    private CaptchaManager captchaManager;

    @Autowired
    private MockMvc mockMvc;

    @DisplayName("register")
    @Nested
    class register {

        @DisplayName("정상적인 유저가 요청하면 회원가입에 성공한다.")
        @Test
        void it_registers_user_when_user_request_is_valid() throws Exception {
            // given
            var testToken = "testToken";
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            doReturn(false).when(userContext).isAuthed();

            var testUserInfo = AuthUserInfo
                    .builder()
                    .uid("testUid")
                    .displayName("testDisplayName")
                    .profileImage("testProfileImage")
                    .email("testEmail")
                    .provider("testProvider")
                    .build();

            doReturn(testUserInfo).when(authManager).authenticate(testToken);

            var request = UserCreateRequestServiceDto
                    .builder()
                    .uid(testUserInfo.getUid())
                    .name(testUserInfo.getDisplayName())
                    .profileImage(testUserInfo.getProfileImage())
                    .email(testUserInfo.getEmail())
                    .provider(testUserInfo.getProvider())
                    .build();
            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/auth?captchaToken=captchaToken")
//                    .param("captchaToken", "captchaToken")
                    .header("Authorization", "Bearer " + testToken));

            // then
            result.andExpect(status().isOk())
                    .andDo(document("post/updatePostStatus",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Auth API")
                                    .summary("회원 가입 합니다. (AUTH-LEVEL : NONE)")
                                    .queryParameters(
                                            parameterWithName("captchaToken").description("캡차 토큰")
                                    )
                                    .build())
                    ));
            verify(userService).register(request);
        }

        @DisplayName("프로필 이미지가 없으면 기본 이미지로 설정한다.")
        @Test
        void it_sets_default_profile_image_when_profile_image_is_empty() throws Exception {
            // given
            var testToken = "testToken";
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            doReturn(false).when(userContext).isAuthed();

            var testUserInfo = AuthUserInfo
                    .builder()
                    .uid("testUid")
                    .displayName("testDisplayName")
                    .profileImage("")
                    .email("testEmail")
                    .provider("testProvider")
                    .build();

            doReturn(testUserInfo).when(authManager).authenticate(testToken);

            var defaultProfileImage = "defaultProfileImage";

            doReturn(defaultProfileImage).when(authConfig).getDefaultProfileImg();

            var request = UserCreateRequestServiceDto
                    .builder()
                    .uid(testUserInfo.getUid())
                    .name(testUserInfo.getDisplayName())
                    .profileImage(defaultProfileImage)
                    .email(testUserInfo.getEmail())
                    .provider(testUserInfo.getProvider())
                    .build();
            // when
            var result = mockMvc.perform(post("/api/v1/auth")
                    .param("captchaToken", "captchaToken")
                    .header("Authorization", "Bearer " + testToken));

            // then
            result.andExpect(status().isOk());
            verify(userService).register(request);
        }

        @DisplayName("토큰 decode결과에 이메일 정보가 없으면 예외를 던진다.")
        @Test
        void it_throws_exception_when_email_is_empty() throws Exception {
            // given
            var testToken = "test";
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            doReturn(false).when(userContext).isAuthed();

            var testUserInfo = AuthUserInfo
                    .builder()
                    .uid("testUid")
                    .displayName("testDisplayName")
                    .profileImage("testProfileImage")
                    .email("")
                    .provider("testProvider")
                    .build();

            doReturn(testUserInfo).when(authManager).authenticate(testToken);

            // when
            var result = mockMvc.perform(post("/api/v1/auth")
                    .param("captchaToken", "captchaToken")
                    .header("Authorization", "Bearer " + testToken));

            // then
            result.andExpect(status().isInternalServerError());
        }

        @DisplayName("토큰 decode결과에 이름 정보가 없으면 예외를 던진다.")
        @Test
        void it_throws_exception_when_name_is_empty() throws Exception {
            // given
            var testToken = "test";
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            doReturn(false).when(userContext).isAuthed();

            var testUserInfo = AuthUserInfo
                    .builder()
                    .uid("testUid")
                    .displayName("")
                    .profileImage("testProfileImage")
                    .email("testEmail")
                    .provider("testProvider")
                    .build();

            doReturn(testUserInfo).when(authManager).authenticate(testToken);

            // when
            var result = mockMvc.perform(post("/api/v1/auth")
                    .param("captchaToken", "captchaToken")
                    .header("Authorization", "Bearer " + testToken));

            // then
            result.andExpect(status().isInternalServerError());
        }

        @DisplayName("유저가 이미 로그인 되어있으면 예외를 던진다.")
        @Test
        void it_throws_exception_when_user_is_already_logged_in() throws Exception {
            // given
            var testToken = "test";
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            doReturn(true).when(userContext).isAuthed();

            // when
            var result = mockMvc.perform(post("/api/v1/auth")
                    .param("captchaToken", "captchaToken")
                    .header("Authorization", "Bearer " + testToken));

            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("토큰이 없으면 예외를 던진다.")
        @Test
        void it_throws_exception_when_token_is_empty() throws Exception {
            // given
            var testToken = "";
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            doReturn(false).when(userContext).isAuthed();

            // when
            var result = mockMvc.perform(post("/api/v1/auth")
                    .param("captchaToken", "captchaToken"));

            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("토큰이 유효하지 않으면 예외를 던진다.")
        @Test
        void it_throws_exception_when_token_is_invalid() throws Exception {
            // given
            var testToken = "test";
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            doReturn(false).when(userContext).isAuthed();
            doReturn(null).when(authManager).authenticate(testToken);

            // when
            var result = mockMvc.perform(post("/api/v1/auth")
                    .param("captchaToken", "captchaToken")
                    .header("Authorization", "Bearer " + testToken));

            // then
            result.andExpect(status().isForbidden());
        }

        @DisplayName("토큰이 Bearer로 시작하지 않으면 예외를 던진다.")
        @Test
        void it_throws_exception_when_token_does_not_start_with_bearer() throws Exception {
            // given
            var testToken = "test";
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            doReturn(false).when(userContext).isAuthed();

            // when
            var result = mockMvc.perform(post("/api/v1/auth")
                    .param("captchaToken", "captchaToken")
                    .header("Authorization", "test"));

            // then
            result.andExpect(status().isForbidden());
        }

    }


}
