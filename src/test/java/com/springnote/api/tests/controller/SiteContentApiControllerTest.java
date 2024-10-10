package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.dto.assembler.siteContent.SiteContentResponseCommonDtoAssembler;
import com.springnote.api.dto.siteContent.controller.SiteContentCreateRequestControllerDto;
import com.springnote.api.dto.siteContent.controller.SiteContentUpdateRequestControllerDto;
import com.springnote.api.dto.siteContent.service.SiteContentCreateRequestServiceDto;
import com.springnote.api.dto.siteContent.service.SiteContentUpdateRequestServiceDto;
import com.springnote.api.service.SiteContentService;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.web.controller.SiteContentApiController;
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
import static com.springnote.api.testUtils.dataFactory.siteContent.SiteContentDtoTestDataFactory.createSiteContentResponseCommonDto;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SiteContentApiController.class)
@DisplayName("Controller Test - SiteContentApiController")
public class SiteContentApiControllerTest extends ControllerTestTemplate {

    @Autowired
    private SiteContentApiController siteContentApiController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SiteContentService siteContentService;

    @SpyBean
    private SiteContentResponseCommonDtoAssembler assembler;

    @DisplayName("getSiteContentByKey")
    @Nested
    class getSiteContentByKey {

        @DisplayName("올바른 키를 입력했을 때 올바른 사이트 컨텐츠를 반환한다.")
        @Test
        void getSiteContentByKey_ValidKey_ReturnsSiteContent() throws Exception {
            // given
            var validKey = "test";

            var expectedSiteContent = createSiteContentResponseCommonDto();

            doReturn(expectedSiteContent).when(siteContentService).getSiteContentById(validKey);

            // when

            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/site-content/{key}", validKey));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.key").value(expectedSiteContent.getKey()))
                    .andExpect(jsonPath("$.content").value(expectedSiteContent.getContent()))
                    .andDo(print())
                    .andDo(document("siteContent/getSiteContentByKey",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("SiteContent API")
                                    .summary("사이트 콘텐츠 ID로 포스트를 조회합니다.")
                                    .pathParameters(
                                            parameterWithName("key").description("사이트 콘텐츠의 키")
                                    )
                                    .responseFields(
                                            fieldWithPath("key").type(STRING).description("사이트 콘텐츠의 키"),
                                            fieldWithPath("content").type(STRING).description("사이트 콘텐츠의 내용"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .responseSchema(Schema.schema("SiteContent.Response"))
                                    .build())));

        }

        private static Stream<Arguments> provideInvalidKeys() {
            return Stream.of(
                    Arguments.of("T T", "공백이 포함된 키"),
                    Arguments.of("w".repeat(301), "300자가 넘는 키")
            );
        }

        @DisplayName("유효하지 않은 키를 입력했을 때 400 에러를 반환한다.")
        @MethodSource("provideInvalidKeys")
        @ParameterizedTest(name = "{index} : {1} 가 주어였을 때")
        void getSiteContentByKey_InvalidKey_ReturnsBadRequest(String invalidKey, String description) throws Exception {
            // given
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/site-content/{key}", invalidKey));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

    }

    @DisplayName("createSiteContent")
    @Nested
    class createSiteContent {

        @DisplayName("정상적인 사이트 컨텐츠 생성 요청이 주어지면, 사이트 컨텐츠를 생성한다.")
        @Test
        void createSiteContent_ValidRequest_CreatesSiteContent() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var requestDto = SiteContentCreateRequestControllerDto.builder()
                    .key("test")
                    .content("test content")
                    .build();

            var serviceDto = SiteContentCreateRequestServiceDto.builder()
                    .key("test")
                    .value("test content")
                    .build();

            var expectedSiteContent = createSiteContentResponseCommonDto();

            doReturn(expectedSiteContent).when(siteContentService).create(serviceDto);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/site-content")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(requestDto)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.key").value(expectedSiteContent.getKey()))
                    .andExpect(jsonPath("$.content").value(expectedSiteContent.getContent()))
                    .andDo(print())
                    .andDo(document("siteContent/createSiteContent",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("SiteContent API")
                                    .summary("사이트 콘텐츠를 생성합니다. (AUTH_LEVEL: ADMIN)")
                                    .requestFields(
                                            fieldWithPath("key").type(STRING).description("사이트 콘텐츠의 키 (공백 없이  1~300자)"),
                                            fieldWithPath("content").type(STRING).description("사이트 콘텐츠의 내용 (1~30000자)")
                                    )
                                    .responseFields(
                                            fieldWithPath("key").type(STRING).description("사이트 콘텐츠의 키"),
                                            fieldWithPath("content").type(STRING).description("사이트 콘텐츠의 내용"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .requestSchema(Schema.schema("SiteContent.CreateRequest"))
                                    .responseSchema(Schema.schema("SiteContent.Response"))
                                    .build())));
        }

        private static Stream<Arguments> provideInvalidSiteContentCreateRequest() {
            var hasBlankKey = SiteContentCreateRequestControllerDto.builder()
                    .key("T T")
                    .content("test content")
                    .build();

            var emptyKey = SiteContentCreateRequestControllerDto.builder()
                    .key("")
                    .content("test content")
                    .build();

            var tooLongKey = SiteContentCreateRequestControllerDto.builder()
                    .key("w".repeat(301))
                    .content("test content")
                    .build();

            var emptyContent = SiteContentCreateRequestControllerDto.builder()
                    .key("test")
                    .content("")
                    .build();

            var tooLongContent = SiteContentCreateRequestControllerDto.builder()
                    .key("test")
                    .content("w".repeat(30001))
                    .build();

            return Stream.of(
                    Arguments.of(hasBlankKey, "공백이 포함된 키"),
                    Arguments.of(emptyKey, "빈 키"),
                    Arguments.of(tooLongKey, "300자가 넘는 키"),
                    Arguments.of(emptyContent, "빈 본문"),
                    Arguments.of(tooLongContent, "30000자가 넘는 본문")
            );
        }

        @DisplayName("유효하지 않은 사이트 컨텐츠 생성 요청이 주어지면, 400 에러를 반환한다.")
        @MethodSource("provideInvalidSiteContentCreateRequest")
        @ParameterizedTest(name = "{index} : {1} 가 주어였을 때")
        void createSiteContent_InvalidRequest_ReturnsBadRequest(SiteContentCreateRequestControllerDto invalidRequest, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/site-content")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(invalidRequest)));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "유저 권한"),
                    Arguments.of(AuthLevel.NONE, "비인증 상태")
            );
        }

        @DisplayName("권한이 없는 사용자가 사이트 컨텐츠 생성 요청을 하면, 에러를 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}")
        void givenInvalidAuthLevel_whenRequested_thenReturnError(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            var request = SiteContentCreateRequestControllerDto.builder()
                    .key("test")
                    .content("test content")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/site-content")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
        }

    }

    @DisplayName("updateSiteContent")
    @Nested
    class updateSiteContent {

        @DisplayName("정상적인 사이트 컨텐츠 수정 요청이 주어지면, 사이트 컨텐츠를 수정한다.")
        @Test
        void updateSiteContent_ValidRequest_UpdatesSiteContent() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var key = "test";

            var requestDto = SiteContentUpdateRequestControllerDto.builder()
                    .content("test content")
                    .build();

            var serviceDto = SiteContentUpdateRequestServiceDto.builder()
                    .key(key)
                    .value("test content")
                    .build();

            var expectedSiteContent = createSiteContentResponseCommonDto();

            doReturn(expectedSiteContent).when(siteContentService).update(serviceDto);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/site-content/{key}", key)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(requestDto)));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.key").value(expectedSiteContent.getKey()))
                    .andExpect(jsonPath("$.content").value(expectedSiteContent.getContent()))
                    .andDo(print())
                    .andDo(document("siteContent/updateSiteContent",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("SiteContent API")
                                    .summary("사이트 콘텐츠를 수정합니다. (AUTH_LEVEL: ADMIN)")
                                    .pathParameters(
                                            parameterWithName("key").description("사이트 콘텐츠의 키")
                                    )
                                    .requestFields(
                                            fieldWithPath("content").type(STRING).description("사이트 콘텐츠의 내용 (1~30000자)")
                                    )
                                    .responseFields(
                                            fieldWithPath("key").type(STRING).description("사이트 콘텐츠의 키"),
                                            fieldWithPath("content").type(STRING).description("사이트 콘텐츠의 내용"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .requestSchema(Schema.schema("SiteContent.UpdateRequest"))
                                    .responseSchema(Schema.schema("SiteContent.Response"))
                                    .build())));
        }

        private static Stream<Arguments> provideInvalidSiteContentUpdateRequest() {

            var emptyContent = SiteContentUpdateRequestControllerDto.builder()
                    .content("")
                    .build();

            var tooLongContent = SiteContentUpdateRequestControllerDto.builder()
                    .content("w".repeat(30001))
                    .build();

            return Stream.of(

                    Arguments.of(emptyContent, "빈 본문"),
                    Arguments.of(tooLongContent, "30000자가 넘는 본문")
            );
        }

        @DisplayName("유효하지 않은 사이트 컨텐츠 수정 요청이 주어지면, 400 에러를 반환한다.")
        @MethodSource("provideInvalidSiteContentUpdateRequest")
        @ParameterizedTest(name = "{index} : {1} 가 주어였을 때")
        void updateSiteContent_InvalidRequest_ReturnsBadRequest(SiteContentUpdateRequestControllerDto invalidRequest, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/site-content/test")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(invalidRequest)));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "유저 권한"),
                    Arguments.of(AuthLevel.NONE, "비인증 상태")
            );
        }

        @DisplayName("권한이 없는 사용자가 사이트 컨텐츠 수정 요청을 하면, 에러를 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}")
        void givenInvalidAuthLevel_whenRequested_thenReturnError(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            var request = SiteContentUpdateRequestControllerDto.builder()
                    .content("test content")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/site-content/test")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidKeys() {
            return Stream.of(
                    Arguments.of("T T", "공백이 포함된 키"),
                    Arguments.of("w".repeat(301), "300자가 넘는 키")
            );
        }

        @DisplayName("유효하지 않은 키를 입력했을 때 400 에러를 반환한다.")
        @MethodSource("provideInvalidKeys")
        @ParameterizedTest(name = "{index} : {1} 가 주어였을 때")
        void updateSiteContent_InvalidKey_ReturnsBadRequest(String invalidKey, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/site-content/{key}", invalidKey));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @DisplayName("deleteSiteContent")
    @Nested
    class deleteSiteContent {

        @DisplayName("정상적인 사이트 컨텐츠 삭제 요청이 주어지면, 사이트 컨텐츠를 삭제한다.")
        @Test
        void deleteSiteContent_ValidRequest_DeletesSiteContent() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var key = "test";

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/site-content/{key}", key));

            // then
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("siteContent/deleteSiteContent",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("SiteContent API")
                                    .summary("사이트 콘텐츠를 삭제합니다. (AUTH_LEVEL: ADMIN)")
                                    .pathParameters(
                                            parameterWithName("key").description("사이트 콘텐츠의 키")
                                    )
                                    .build())));
        }

        private static Stream<Arguments> provideInvalidKeys() {
            return Stream.of(
                    Arguments.of("T T", "공백이 포함된 키"),
                    Arguments.of("w".repeat(301), "300자가 넘는 키")
            );
        }

        @DisplayName("유효하지 않은 키를 입력했을 때 400 에러를 반환한다.")
        @MethodSource("provideInvalidKeys")
        @ParameterizedTest(name = "{index} : {1} 가 주어였을 때")
        void deleteSiteContent_InvalidKey_ReturnsBadRequest(String invalidKey, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/site-content/{key}", invalidKey));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "유저 권한"),
                    Arguments.of(AuthLevel.NONE, "비인증 상태")
            );
        }

        @DisplayName("권한이 없는 사용자가 사이트 컨텐츠 삭제 요청을 하면, 에러를 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}")
        void givenInvalidAuthLevel_whenRequested_thenReturnError(AuthLevel authLevel, String description) throws Exception {
            // given
            var validKey = "test";
            createUserContextReturns(userContext, authLevel);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/site-content/{key}", validKey));

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
        }
    }
}
