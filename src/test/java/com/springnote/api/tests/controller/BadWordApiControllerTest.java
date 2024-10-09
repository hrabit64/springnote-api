package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.dto.assembler.badWord.BadWordResponseCommonDtoAssembler;
import com.springnote.api.dto.badWord.controller.BadWordCreateRequestControllerDto;
import com.springnote.api.dto.badWord.service.BadWordCreateRequestServiceDto;
import com.springnote.api.service.BadWordService;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.utils.badWord.BadWordFilter;
import com.springnote.api.web.controller.BadWordApiController;
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
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Stream;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createPageObject;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createUserContextReturns;
import static com.springnote.api.testUtils.dataFactory.badWord.BadWordDtoTestDataFactory.createBadWordResponseDto;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller Test - BadWordApiController")
@WebMvcTest(BadWordApiController.class)
public class BadWordApiControllerTest extends ControllerTestTemplate {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BadWordApiController badWordApiController;

    @MockBean
    private BadWordService badWordService;

    @MockBean
    private BadWordFilter badWordFilter;

    @SpyBean
    private BadWordResponseCommonDtoAssembler assembler;

    @DisplayName("getBadWords")
    @Nested
    class getBadWords {

        @DisplayName("금칙어 목록을 조회한다.")
        @Test
        void getBadWords_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var badWord = createBadWordResponseDto();
            var pageable = createPageObject(List.of(badWord), 0, 20, "id", Sort.Direction.DESC);

            doReturn(pageable).when(badWordService).getAll(PageRequest.of(0, 20, Sort.Direction.DESC, "id"), null);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/bad-word"));
            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.bad_words[0].id").value(badWord.getId()))
                    .andExpect(jsonPath("$._embedded.bad_words[0].word").value(badWord.getWord()))
                    .andExpect(jsonPath("$._embedded.bad_words[0].is_bad_word").value(badWord.getIsBadWord()))
                    .andDo(document("badWord/getBadWords",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("BadWord API")
                                    .summary("금칙어 정보를 조회합니다. (AUTH-LEVEL : ADMIN)")
                                    .description(
                                            "* 사용 가능 쿼리 파라미터 (대소문자 구분 없음)\n" +
                                                    "\n" +
                                                    "|  키  |                 설명                  |        제약        |\n" +
                                                    "| :--: | :-----------------------------------: | :----------------: |\n" +
                                                    "| type |     true 비속어, false  허용단어      | 1 종류만 입력 가능 |\n" +
                                                    "| word | 해당 word를 포함하는 단어를 찾습니다. | 공백이 없는 2~10자 |\n" +
                                                    "\n" +
                                                    "* 페이징\n" +
                                                    "\n" +
                                                    "해당 API는 페이징을 지원합니다.\n" +
                                                    "\n" +
                                                    "|  키  |          설명           |            제약            |      기본값      |\n" +
                                                    "| :--: | :---------------------: | :------------------------: | :--------------: |\n" +
                                                    "| size |      페이징 사이즈      |        최대 100까지        |        20        |\n" +
                                                    "| page | 페이지 넘버 *0부터 시작 |                            |        0         |\n" +
                                                    "| sort |        정렬 옵션        | *아래 사용 가능 키만 가능* | id;DESC |\n" +
                                                    "\n" +
                                                    "사용 가능한 sort키는 아래와 같습니다. *모든 Sort 사용시 방향을 지정하지 않으면 ASC 로 동작합니다. (대소문자 구분 없음)*\n" +
                                                    "\n" +
                                                    "|        키        |     설명      |\n" +
                                                    "| :--------------: | :-----------: |\n" +
                                                    "|        id        |   금칙어 id  |\n" +
                                                    "|      word       |  금칙어 단어  |\n" +
                                                    "|     type      |  금칙어 유형  |\n" +
                                                    "\n"
                                    )
                                    .responseFields(
                                            subsectionWithPath("_embedded.bad_words[]._links").ignored(),
                                            subsectionWithPath("_links").ignored(),
                                            fieldWithPath("_embedded.bad_words[].id").type(NUMBER).description("금칙어 ID"),
                                            fieldWithPath("_embedded.bad_words[].word").type(STRING).description("금칙어"),
                                            fieldWithPath("_embedded.bad_words[].is_bad_word").type(BOOLEAN).description("금칙어 여부"),
                                            fieldWithPath("page.size").type(NUMBER).description("페이지 크기"),
                                            fieldWithPath("page.totalElements").type(NUMBER).description("전체 요소 수"),
                                            fieldWithPath("page.totalPages").type(NUMBER).description("전체 페이지 수"),
                                            fieldWithPath("page.number").type(NUMBER).description("현재 페이지 번호")

                                    )
                                    .responseSchema(Schema.schema("BadWord.PagedResponse"))
                                    .build())
                    ));


            verify(badWordService).getAll(PageRequest.of(0, 20, Sort.Direction.DESC, "id"), null);
        }

        // type 있을 때
        @DisplayName("type 조건이 있을 때 금칙어 목록을 조회한다.")
        @Test
        void getBadWords_success_with_type() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var badWord = createBadWordResponseDto();
            var pageable = createPageObject(List.of(badWord), 0, 20, "id", Sort.Direction.DESC);

            doReturn(pageable).when(badWordService).getAll(PageRequest.of(0, 20, Sort.Direction.DESC, "id"), true);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bad-word?type=true"));
            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.bad_words[0].id").value(badWord.getId()))
                    .andExpect(jsonPath("$._embedded.bad_words[0].word").value(badWord.getWord()))
                    .andExpect(jsonPath("$._embedded.bad_words[0].is_bad_word").value(badWord.getIsBadWord()));

            verify(badWordService).getAll(PageRequest.of(0, 20, Sort.Direction.DESC, "id"), true);
        }

        // word 있을 때
        @DisplayName("word 조건이 있을 때 금칙어 목록을 조회한다.")
        @Test
        void getBadWords_success_with_word() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var badWord = createBadWordResponseDto();
            var pageable = createPageObject(List.of(badWord), 0, 20, "id", Sort.Direction.DESC);

            doReturn(pageable).when(badWordService).getAllByWord("test", null, PageRequest.of(0, 20, Sort.Direction.DESC, "id"));

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bad-word?word=test"));
            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.bad_words[0].id").value(badWord.getId()))
                    .andExpect(jsonPath("$._embedded.bad_words[0].word").value(badWord.getWord()))
                    .andExpect(jsonPath("$._embedded.bad_words[0].is_bad_word").value(badWord.getIsBadWord()));

            verify(badWordService).getAllByWord("test", null, PageRequest.of(0, 20, Sort.Direction.DESC, "id"));
        }

        private static Stream<Arguments> provideInvalidWord() {
            return Stream.of(
                    Arguments.of("w", "2자 미만 검색어"),
                    Arguments.of("w".repeat(11), "10자가 넘는 검색어")
            );
        }

        @DisplayName("word 조건이 유효하지 않을 때 400 에러를 반환한다.")
        @MethodSource("provideInvalidWord")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void getBadWords_fail_with_invalid_word(String word, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bad-word?word=" + word));
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("페이징 사이즈가 제한 보다 크면 400 에러를 반환한다.")
        @Test
        void getBadWords_fail_with_invalid_page_size() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bad-word?page=0&size=101"));
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        //유효한 정렬키
        private static Stream<Arguments> provideValidSortKey() {
            return Stream.of(
                    Arguments.of("id"),
                    Arguments.of("word"),
                    Arguments.of("type")
            );
        }

        @DisplayName("정렬 키가 유효할 때 금칙어 목록을 조회한다.")
        @MethodSource("provideValidSortKey")
        @ParameterizedTest(name = "{index} : {0} 가 주어졌을 때")
        void getBadWords_success_with_valid_sort_key(String sortKey) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var badWord = createBadWordResponseDto();
            var pageable = createPageObject(List.of(badWord), 0, 20, sortKey, Sort.Direction.ASC);

            doReturn(pageable).when(badWordService).getAll(PageRequest.of(0, 20, Sort.Direction.ASC, sortKey), null);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bad-word?sort=" + sortKey));
            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.bad_words[0].id").value(badWord.getId()))
                    .andExpect(jsonPath("$._embedded.bad_words[0].word").value(badWord.getWord()))
                    .andExpect(jsonPath("$._embedded.bad_words[0].is_bad_word").value(badWord.getIsBadWord()));

            verify(badWordService).getAll(PageRequest.of(0, 20, Sort.Direction.ASC, sortKey), null);
        }


        @DisplayName("정렬 키가 유효하지 않으면 400 에러를 반환한다.")
        @Test
        void getBadWords_fail_with_invalid_sort_key() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bad-word?sort=invalid"));
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("권한이 없으면 403 에러를 반환한다.")
        @Test
        void getBadWords_fail_with_no_permission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bad-word"));
            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @DisplayName("refreshBadWords")
    @Nested
    class refreshBadWords {

        @DisplayName("금칙어 목록을 갱신한다.")
        @Test
        void refreshBadWords_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/bad-word/refresh"));
            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("badWord/refreshBadWords",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("BadWord API")
                                    .summary("금칙어 목록을 갱신합니다. (AUTH-LEVEL : ADMIN)")
                                    .responseFields(
                                            fieldWithPath("message").type(STRING).description("메시지")
                                    )
                                    .responseSchema(Schema.schema("BadWord.Refresh"))
                                    .build())
                    ));

            verify(badWordFilter).refresh();
        }

        @DisplayName("권한이 없으면 403 에러를 반환한다.")
        @Test
        void refreshBadWords_fail_with_no_permission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bad-word/refresh"));
            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }


    @DisplayName("getBadWordById")
    @Nested
    class getBadWord {

        @DisplayName("올바른 Id가 주어지면 금칙어를 조회한다.")
        @Test
        void getBadWord_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var badWord = createBadWordResponseDto();

            doReturn(badWord).when(badWordService).getById(1L);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/bad-word/{id}", 1L));
            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(badWord.getId()))
                    .andExpect(jsonPath("$.word").value(badWord.getWord()))
                    .andExpect(jsonPath("$.is_bad_word").value(badWord.getIsBadWord()))
                    .andDo(document("badWord/getBadWordById",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("BadWord API")
                                    .summary("해당하는 ID의 금칙어 정보를 조회합니다. (AUTH-LEVEL : ADMIN)")
                                    .pathParameters(
                                            parameterWithName("id").description("금칙어 ID")
                                    )
                                    .responseFields(
                                            subsectionWithPath("_links").ignored(),
                                            fieldWithPath("id").type(NUMBER).description("금칙어 ID"),
                                            fieldWithPath("word").type(STRING).description("금칙어"),
                                            fieldWithPath("is_bad_word").type(BOOLEAN).description("금칙어 여부")
                                    )
                                    .responseSchema(Schema.schema("BadWord.Response"))
                                    .build())
                    ));

            verify(badWordService).getById(1L);
        }

        @DisplayName("권한이 없으면 403 에러를 반환한다.")
        @Test
        void getBadWord_fail_with_no_permission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/bad-word/1"));
            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

    }

    @DisplayName("createBadWord")
    @Nested
    class createBadWord {

        @DisplayName("정상적인 요청이 들어오면 금칙어를 생성한다.")
        @Test
        void createBadWord_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var validRequest = BadWordCreateRequestControllerDto.builder()
                    .word("15자이하")
                    .isBadWord(true)
                    .build();

            var serviceDto = BadWordCreateRequestServiceDto
                    .builder()
                    .word("15자이하")
                    .isBadWord(true)
                    .build();

            var badWord = createBadWordResponseDto();

            doReturn(badWord).when(badWordService).create(serviceDto);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/bad-word")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest)));
            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(badWord.getId()))
                    .andExpect(jsonPath("$.word").value(badWord.getWord()))
                    .andExpect(jsonPath("$.is_bad_word").value(badWord.getIsBadWord()))
                    .andDo(document("badWord/createBadWord",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("BadWord API")
                                    .summary("금칙어를 생성합니다. (AUTH-LEVEL : ADMIN)")
                                    .requestFields(
                                            fieldWithPath("word").type(STRING).description("공백 없는 단어 (2자 이상, 15자 이하)"),
                                            fieldWithPath("is_bad_word").type(BOOLEAN).description("금칙어 여부")
                                    )
                                    .responseFields(
                                            subsectionWithPath("_links").ignored(),
                                            fieldWithPath("id").type(NUMBER).description("금칙어 ID"),
                                            fieldWithPath("word").type(STRING).description("금칙어"),
                                            fieldWithPath("is_bad_word").type(BOOLEAN).description("금칙어 여부")
                                    )
                                    .requestSchema(Schema.schema("BadWord.Create"))
                                    .responseSchema(Schema.schema("BadWord.Response"))
                                    .build())
                    ));

            verify(badWordService).create(serviceDto);
        }

        private static Stream<Arguments> provideInvalidRequest() {
            var tooShort = BadWordCreateRequestControllerDto.builder()
                    .word("w")
                    .isBadWord(true)
                    .build();
            var tooLong = BadWordCreateRequestControllerDto.builder()
                    .word("w".repeat(16))
                    .isBadWord(true)
                    .build();

            var nullType = BadWordCreateRequestControllerDto.builder()
                    .word("금칙어")
                    .isBadWord(null)
                    .build();
            var hasBlank = BadWordCreateRequestControllerDto.builder()
                    .word("금칙 어 ")
                    .isBadWord(true)
                    .build();
            return Stream.of(
                    Arguments.of(tooShort, "2자 미만 단어"),
                    Arguments.of(tooLong, "15자가 넘는 단어"),
                    Arguments.of(nullType, "누락된 타입"),
                    Arguments.of(hasBlank, "공백이 있는 단어")
            );
        }

        @DisplayName("요청이 유효하지 않으면 400 에러를 반환한다.")
        @MethodSource("provideInvalidRequest")
        @ParameterizedTest(name = "{index} : {1} 이(가) 주어졌을 때")
        void createBadWord_fail_with_invalid_request(BadWordCreateRequestControllerDto request, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bad-word")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(request)));
            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("권한이 없으면 403 에러를 반환한다.")
        @Test
        void createBadWord_fail_with_no_permission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            var request = BadWordCreateRequestControllerDto.builder()
                    .word("금칙어")
                    .isBadWord(true)
                    .build();
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/bad-word")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(request)));

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

    }

    @DisplayName("deleteBadWord")
    @Nested
    class deleteBadWord {

        @DisplayName("정상적인 요청이 들어오면 금칙어를 삭제한다.")
        @Test
        void deleteBadWord_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/bad-word/{id}", 1L));

            // then
            result.andDo(print())
                    .andExpect(status().isNoContent())
                    .andDo(document("badWord/deleteBadWord",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("BadWord API")
                                    .summary("해당하는 ID의 금칙어 정보를 삭제합니다. (AUTH-LEVEL : ADMIN)")
                                    .pathParameters(
                                            parameterWithName("id").description("금칙어 ID")
                                    )
                                    .build())
                    ));

            verify(badWordService).delete(1L);
        }

        @DisplayName("권한이 없으면 403 에러를 반환한다.")
        @Test
        void deleteBadWord_fail_with_no_permission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/bad-word/1"));
            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

    }
}
