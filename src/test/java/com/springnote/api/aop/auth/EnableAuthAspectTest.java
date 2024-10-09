package com.springnote.api.aop.auth;

import com.springnote.api.config.JacksonConfig;
import com.springnote.api.config.TestAspectConfig;
import com.springnote.api.dto.user.common.UserResponseCommonDto;
import com.springnote.api.security.auth.AuthManager;
import com.springnote.api.security.auth.AuthUserInfo;
import com.springnote.api.service.UserService;
import com.springnote.api.testUtils.template.UseGeneralTestConfig;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.time.TimeHelper;
import com.springnote.api.web.controller.TestApiController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TestApiController.class)
@Import({TestAspectConfig.class, JacksonConfig.class, EnableAuthAspect.class, TimeHelper.class})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@UseGeneralTestConfig
class EnableAuthAspectTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private UserContext userContext;

    @MockBean
    private AuthManager authManager;

    @MockBean
    private UserService userService;


    @DisplayName("정상적인 요청이 들어왔을 때, 인증에 성공하는지 확인한다.")
    @Test
    void testSuccessAuth() throws Exception {
        // given
        var validUserInfo = AuthUserInfo.builder()
                .uid("test")
                .build();
        doReturn(validUserInfo).when(authManager).authenticate("test");

        var validUser = UserResponseCommonDto.builder()
                .id("test")
                .isEnabled(true)
                .isAdmin(false)
                .build();

        doReturn(validUser).when(userService).getUser("test");

        var validToken = "Bearer test";
        // when
        var result = mockMvc.perform(MockMvcRequestBuilders.get("/test/auth")
                .header("Authorization", validToken));
        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(result1 -> {
                    var contentAsString = result1.getResponse().getContentAsString();
                    assert contentAsString.equals("authed");
                });

    }

    private static Stream<Arguments> provideInvalidRequest() {
        var validUserInfo = AuthUserInfo.builder()
                .uid("test")
                .build();

        var validUser = UserResponseCommonDto.builder()
                .id("test")
                .isEnabled(true)
                .isAdmin(false)
                .build();

        var disabledUser = UserResponseCommonDto.builder()
                .id("test")
                .isEnabled(false)
                .isAdmin(false)
                .build();

        // 토큰 결과 , 유저 서비스 결과, 인증 헤더
        return Stream.of(
                Arguments.of(null, null, "Bearer test", "잘못된 인증 토큰"),
                Arguments.of(validUserInfo, null, "Bearer test", "회원 정보가 없는 토큰"),
                Arguments.of(validUserInfo, validUser, "test", "잘못된 토큰 형식"),
                Arguments.of(validUserInfo, disabledUser, "Bearer test", "비활성화된 사용자"),
                Arguments.of(validUserInfo, validUser, null, "인증 헤더가 없음")
        );
    }

    @DisplayName("비정상적인 요청이 들어왔을 때, 인증에 실패하는지 확인한다.")
    @MethodSource("provideInvalidRequest")
    @ParameterizedTest(name = "{index} : {3}가 주어졌을 때")
    void testFailAuth(AuthUserInfo authUserInfo, UserResponseCommonDto userResponseCommonDto, String token, String message) throws Exception {
        // given
        doReturn(authUserInfo).when(authManager).authenticate(token);
        doReturn(userResponseCommonDto).when(userService).getUser("test");
        // when

        ResultActions result;

        if (token == null) {
            result = mockMvc.perform(MockMvcRequestBuilders.get("/test/auth"));
        } else {
            result = mockMvc.perform(MockMvcRequestBuilders.get("/test/auth")
                    .header("Authorization", token));
        }

        // then
        result.andDo(print())
                .andExpect(status().isOk())
                .andExpect(result1 -> {
                    var contentAsString = result1.getResponse().getContentAsString();
                    assert contentAsString.equals("not authed");
                });
    }
}