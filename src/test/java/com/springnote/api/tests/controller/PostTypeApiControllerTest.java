package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.springnote.api.service.PostTypeService;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.web.controller.PostTypeApiController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.stream.Stream;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createPageObject;
import static com.springnote.api.testUtils.dataFactory.postType.PostTypeDtoTestDataFactory.createPostTypeResponseDto;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostTypeApiController.class)
@DisplayName("Controller Test - PostTypeApiController")
public class PostTypeApiControllerTest extends ControllerTestTemplate {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private PostTypeApiController postTypeApiController;

    @MockBean
    private PostTypeService postTypeService;

    @DisplayName("getPostTypes")
    @Nested
    class getPostTypes {

        @DisplayName("포스트 타입을 조회한다.")
        @Test
        void getPostTypes_success() throws Exception {
            // given
            var targetPostType = createPostTypeResponseDto();
            var pagedPostType = createPageObject(List.of(targetPostType), 0, 20, "id", Sort.Direction.DESC);

            doReturn(pagedPostType).when(postTypeService).get(PageRequest.of(0, 20, Sort.Direction.DESC, "id"));

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post-type"));

            // then
            result.andDo(print())
                    .andExpect(jsonPath("$._embedded.post_types[0].id").value(targetPostType.getId()))
                    .andExpect(jsonPath("$._embedded.post_types[0].name").value(targetPostType.getName()))
                    .andExpect(status().isOk())
                    .andDo(document("postType/getPostTypes",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("PostType API")
                                    .summary("포스트 타입을 가져옵니다.")
                                    .description("* 페이징\n" +
                                            "\n" +
                                            "해당 API는 페이징을 지원합니다.\n" +
                                            "\n" +
                                            "|  키  |          설명           |            제약            |      기본값      |\n" +
                                            "| :--: | :---------------------: | :------------------------: | :--------------: |\n" +
                                            "| size |      페이징 사이즈      |        최대 20까지        |        20        |\n" +
                                            "| page | 페이지 넘버 *0부터 시작 |                            |        0         |\n" +
                                            "| sort |        정렬 옵션        | *아래 사용 가능 키만 가능* | id;DESC |\n" +
                                            "\n" +
                                            "사용 가능한 sort키는 아래와 같습니다. *모든 Sort 사용시 방향을 지정하지 않으면 ASC 로 동작합니다. (대소문자 구분 없음)*\n" +
                                            "\n" +
                                            "|        키        |     설명      |\n" +
                                            "| :--------------: | :-----------: |\n" +
                                            "|        id        |   타입 ID   |\n" +
                                            "|       name       |  이름  |\n" +
                                            "\n")
                                    .responseFields(
                                            fieldWithPath("_embedded.post_types[].id").type(NUMBER).description("포스트 타입 ID"),
                                            fieldWithPath("_embedded.post_types[].name").type(STRING).description("포스트 타입 이름"),
                                            subsectionWithPath("_links").ignored(),
                                            subsectionWithPath("page").ignored()
                                    )
                                    .responseSchema(Schema.schema("PostType.Response"))
                                    .build())
                    ));

            verify(postTypeService).get(PageRequest.of(0, 20, Sort.Direction.DESC, "id"));
        }

        private static Stream<Arguments> provideSortKeys() {
            return Stream.of(
                    Arguments.of("name"),
                    Arguments.of("id")
            );
        }

        @DisplayName("올바른 정렬 키를 입력했을 때, 올바른 결과를 반환한다.")
        @MethodSource("provideSortKeys")
        @ParameterizedTest(name = "{index} : {0} 가 주어졌을 때")
        void getPostTypes_success_withValidSortKey(String sortKey) throws Exception {
            // given
            var targetPostType = createPostTypeResponseDto();
            var pagedPostType = createPageObject(List.of(targetPostType), 0, 20, sortKey, Sort.Direction.ASC);

            doReturn(pagedPostType).when(postTypeService).get(PageRequest.of(0, 20, Sort.Direction.ASC, sortKey));

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post-type?sort=" + sortKey));

            // then
            result.andDo(print())
                    .andExpect(jsonPath("$._embedded.post_types[0].id").value(targetPostType.getId()))
                    .andExpect(jsonPath("$._embedded.post_types[0].name").value(targetPostType.getName()))
                    .andExpect(status().isOk());

            verify(postTypeService).get(PageRequest.of(0, 20, Sort.Direction.ASC, sortKey));
        }

        @DisplayName("올바르지 않은 정렬 키를 입력했을 때, 400 Bad Request를 반환한다.")
        @Test
        void getPostTypes_fail_withInvalidSortKey() throws Exception {
            // given
            var invalidSortKey = "invalidSortKey";

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post-type?sort=" + invalidSortKey));

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("올바르지 않은 페이지 사이즈를 입력했을 때, 400 Bad Request를 반환한다.")
        @Test
        void getPostTypes_fail_withInvalidPageSize() throws Exception {
            // given
            var invalidPageSize = 21;

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post-type?size=" + invalidPageSize));

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }
}
