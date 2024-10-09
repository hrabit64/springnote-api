package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.dto.assembler.config.ConfigResponseCommonDtoAssembler;
import com.springnote.api.dto.config.common.ConfigResponseCommonDto;
import com.springnote.api.service.ConfigService;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.web.controller.ConfigApiController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.stream.Stream;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createUserContextReturns;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ConfigApiController.class)
@DisplayName("Controller Test - ConfigApiController")
public class ConfigApiControllerTest extends ControllerTestTemplate {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private ConfigApiController configApiController;

    @MockBean
    private ConfigService configService;

    @SpyBean
    private ConfigResponseCommonDtoAssembler assembler;

    @DisplayName("getConfigByKey")
    @Nested
    class getConfigByKey {

        @DisplayName("정상적인 key를 입력했을 때, 해당 key에 대한 값을 반환한다.")
        @Test
        void getConfigByKey_success() throws Exception {
            // given
            var targetKey = "key";
            var targetValue = "value";

            doReturn(targetValue).when(configService).getConfig(targetKey);
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/config/{key}", targetKey));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.key").value(targetKey))
                    .andExpect(jsonPath("$.value").value(targetValue))
                    .andDo(print())
                    .andDo(document("config/getConfigByKey",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Config API")
                                    .summary("설정 값을 가져옵니다. (AUTH-LEVEL : ADMIN)")
                                    .pathParameters(
                                            parameterWithName("key").description("설정 키")
                                    )
                                    .responseFields(
                                            fieldWithPath("key").type(STRING).description("설정 키"),
                                            fieldWithPath("value").type(STRING).description("설정 값"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .responseSchema(Schema.schema("Config.Response"))
                                    .build())
                    ));

            verify(configService).getConfig(targetKey);

        }

        private static Stream<Arguments> provideInvalidKeys() {
            return Stream.of(
                    Arguments.of("w".repeat(301), "300자 보다 긴 key"),
                    Arguments.of("", "1자 미만의 짧은 key"),
                    Arguments.of("T T", "공백이 들어있는 key")

            );
        }

        @DisplayName("잘못된 key를 입력했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidKeys")
        @ParameterizedTest(name = "{index} : {1} 인 잘못된 값이 주어졌을 때")
        void getConfigByKey_noKey(String key, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/config/{key}", key));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());

        }
    }

    @DisplayName("updateConfigByKey")
    @Nested
    class updateConfigByKey {

        @DisplayName("정상적인 key와 value를 입력했을 때, 해당 key에 대한 값을 업데이트한다.")
        @Test
        void updateConfigByKey_success() throws Exception {
            // given
            var targetKey = "key";
            var targetValue = "value";

            var updateResult = ConfigResponseCommonDto.builder()
                    .key(targetKey)
                    .value(targetValue)
                    .build();

            doReturn(updateResult).when(configService).updateConfig(targetKey, targetValue);
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/config/{key}?value=" + targetValue, targetKey));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.key").value(targetKey))
                    .andExpect(jsonPath("$.value").value(targetValue))
                    .andDo(print())
                    .andDo(document("config/updateConfigByKey",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Config API")
                                    .summary("설정 값을 업데이트합니다. (AUTH-LEVEL : ADMIN)")
                                    .pathParameters(
                                            parameterWithName("key").description("설정 키")
                                    )
                                    .queryParameters(
                                            parameterWithName("value").description("설정 값")
                                    )
                                    .responseFields(
                                            fieldWithPath("key").type(STRING).description("설정 키"),
                                            fieldWithPath("value").type(STRING).description("설정 값"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .requestSchema(Schema.schema("Config.Update"))
                                    .responseSchema(Schema.schema("Config.Response"))
                                    .build())
                    ));

            verify(configService).updateConfig(targetKey, targetValue);

        }

        @DisplayName("value가 없을 때, 400 Bad Request를 반환한다.")
        @Test
        void updateConfigByKey_noValue() throws Exception {
            // given
            var targetKey = "key";
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/config/{key}", targetKey));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidItems() {
            return Stream.of(
                    Arguments.of("w".repeat(301), "validValue", "300자 보다 긴 key"),
                    Arguments.of("validKey", "w".repeat(301), "300자 보다 긴 value"),
                    Arguments.of("", "validValue", "1자 미만의 짧은 key"),
                    Arguments.of("validKey", "", "1자 미만의 짧은 value"),
                    Arguments.of("T T", "validValue", "공백이 들어있는 key")

            );
        }

        @DisplayName("잘못된 key를 입력했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidItems")
        @ParameterizedTest(name = "{index} : {2} 인 잘못된 값이 주어졌을 때")
        void updateConfigByKey_noKey(String key, String value, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/config/{key}", key)
                    .param("value", value));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("권한이 없을 때, 403 Forbidden을 반환한다.")
        @Test
        void updateConfigByKey_failWithRole() throws Exception {
            // given
            var targetKey = "key";
            var targetValue = "value";

            createUserContextReturns(userContext, AuthLevel.USER);
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/config/{key}", targetKey)
                    .param("value", targetValue));

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());


        }
    }


}
