package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.dto.assembler.tag.TagResponseDtoAssembler;
import com.springnote.api.dto.tag.controller.TagCreateRequestControllerDto;
import com.springnote.api.dto.tag.controller.TagUpdateRequestControllerDto;
import com.springnote.api.dto.tag.service.TagCreateRequestServiceDto;
import com.springnote.api.dto.tag.service.TagUpdateRequestServiceDto;
import com.springnote.api.service.TagService;
import com.springnote.api.testUtils.dataFactory.tag.TagDtoTestDataFactory;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.web.controller.TagApiController;
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
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagApiController.class)
@DisplayName("Controller Test - TagApiController")
public class TagApiControllerTest extends ControllerTestTemplate {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private TagApiController tagApiController;

    @MockBean
    private TagService tagService;

    @SpyBean
    private TagResponseDtoAssembler assembler;

    @DisplayName("getTags")
    @Nested
    class getTags {

        @DisplayName("태그 이름을 입력하지 않았을 때, 모든 태그를 반환한다.")
        @Test
        void getTags_successWithNoName() throws Exception {
            // given
            var validTag = TagDtoTestDataFactory.createTagResponseDto();
            var pagedTag = createPageObject(List.of(validTag), 0, 10, "id", Sort.Direction.DESC);

            doReturn(pagedTag).when(tagService).getAll(PageRequest.of(0, 10, Sort.Direction.DESC, "id"));
            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/tag"));

            // then
            result
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.tags[0].id").value(validTag.getId()))
                    .andExpect(jsonPath("$._embedded.tags[0].name").value(validTag.getName()))
                    .andDo(print())
                    .andDo(document("tag/getTags",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Tag API")
                                    .summary("Tag 목록을 조회합니다.")
                                    .description(
                                            "* 사용 가능 쿼리 파라미터 (대소문자 구분 없음)\n" +
                                                    "\n" +
                                                    "|       키       |                             설명                             |                 제약                 |                기본값                |\n" +
                                                    "| :------------: | :----------------------------------------------------------: | :----------------------------------: | :----------------------------------: |\n" +
                                                    "|    name     |                     검색할 이름입니다.                    |                   2~100자                   |                  x                   |\n" +
                                                    "\n" +
                                                    "* 페이징\n" +
                                                    "\n" +
                                                    "해당 API는 페이징을 지원합니다.\n" +
                                                    "\n" +
                                                    "|  키  |          설명           |            제약            |      기본값      |\n" +
                                                    "| :--: | :---------------------: | :------------------------: | :--------------: |\n" +
                                                    "| size |      페이징 사이즈      |        최대 20까지        |        10      |\n" +
                                                    "| page | 페이지 넘버 *0부터 시작 |                            |        0         |\n" +
                                                    "| sort |        정렬 옵션        | *아래 사용 가능 키만 가능* | id;DESC |\n" +
                                                    "\n" +
                                                    "사용 가능한 sort키는 아래와 같습니다. *모든 Sort 사용시 방향을 지정하지 않으면 ASC 로 동작합니다. (대소문자 구분 없음)*\n" +
                                                    "\n" +
                                                    "|        키        |     설명      |\n" +
                                                    "| :--------------: | :-----------: |\n" +
                                                    "|        id        |   태그 ID   |\n" +
                                                    "|      name       |  태그 이름  |\n" +
                                                    "\n"
                                    )
                                    .responseFields(
                                            subsectionWithPath("_links").ignored(),
                                            fieldWithPath("page.size").type(NUMBER).description("페이지 크기"),
                                            fieldWithPath("page.totalElements").type(NUMBER).description("전체 요소 수"),
                                            fieldWithPath("page.totalPages").type(NUMBER).description("전체 페이지 수"),
                                            fieldWithPath("page.number").type(NUMBER).description("현재 페이지 번호"),
                                            fieldWithPath("_embedded.tags[].id").type(NUMBER).description("태그 ID"),
                                            fieldWithPath("_embedded.tags[].name").type(SimpleType.STRING).description("태그 이름"),
                                            subsectionWithPath("_embedded.tags[]._links").ignored()
                                    )
                                    .responseSchema(Schema.schema("Tag.PageResponse"))
                                    .build())));

            verify(tagService).getAll(PageRequest.of(0, 10, Sort.Direction.DESC, "id"));
        }

        @DisplayName("태그 이름을 입력했을 때, 해당 이름을 가진 태그를 반환한다.")
        @Test
        void getTags_successWithName() throws Exception {
            // given
            var validTag = TagDtoTestDataFactory.createTagResponseDto();
            var pagedTag = createPageObject(List.of(validTag), 0, 10, "id", Sort.Direction.DESC);

            doReturn(pagedTag).when(tagService).getByName(validTag.getName(), PageRequest.of(0, 10, Sort.Direction.DESC, "id"));
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tag?name=" + validTag.getName()));

            // then
            result
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.tags[0].id").value(validTag.getId()))
                    .andExpect(jsonPath("$._embedded.tags[0].name").value(validTag.getName()))
                    .andDo(print());

            verify(tagService).getByName(validTag.getName(), PageRequest.of(0, 10, Sort.Direction.DESC, "id"));
        }

        private static Stream<Arguments> provideInvalidNames() {
            return Stream.of(
                    Arguments.of("w" .repeat(101), "101자 보다 긴 name"),
                    Arguments.of("", "1자 미만의 짧은 name")
            );
        }

        @DisplayName("잘못된 이름을 입력했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidNames")
        @ParameterizedTest(name = "{index} : {1} 인 잘못된 값이 주어졌을 때")
        void getTags_failWithInvalidName(String name, String description) throws Exception {
            // given

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tag?name=" + name));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
            verifyNoInteractions(tagService);
        }

        private static Stream<Arguments> provideValidPageableSortKey() {
            return Stream.of(
                    Arguments.of("id", "Id로 정렬"),
                    Arguments.of("name", "Name으로 정렬")
            );

        }

        @DisplayName("정상적인 pageable sort key를 입력했을 때, 해당 key로 정렬한다.")
        @MethodSource("provideValidPageableSortKey")
        @ParameterizedTest(name = "{index} : {1} 인 정상적인 값이 주어졌을 때")
        void getTags_successWithValidPageableSortKey(String sortKey, String description) throws Exception {
            // given
            var validTag = TagDtoTestDataFactory.createTagResponseDto();
            var pagedTag = createPageObject(List.of(validTag), 0, 10, sortKey, Sort.Direction.ASC);

            doReturn(pagedTag).when(tagService).getAll(PageRequest.of(0, 10, Sort.Direction.ASC, sortKey));
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tag?sort=" + sortKey));

            // then
            result
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.tags[0].id").value(validTag.getId()))
                    .andExpect(jsonPath("$._embedded.tags[0].name").value(validTag.getName()))
                    .andDo(print());

            verify(tagService).getAll(PageRequest.of(0, 10, Sort.Direction.ASC, sortKey));
        }

        private static Stream<Arguments> provideInvalidPageableSortKey() {
            return Stream.of(
                    Arguments.of("invalid", "유효하지 않은 key")
            );
        }

        @DisplayName("잘못된 pageable sort key를 입력했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidPageableSortKey")
        @ParameterizedTest(name = "{index} : {1} 인 잘못된 값이 주어졌을 때")
        void getTags_failWithInvalidPageableSortKey(String sortKey, String description) throws Exception {
            // given

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tag?sort=" + sortKey));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
            verifyNoInteractions(tagService);

        }

        private static Stream<Arguments> provideValidPageableSize() {
            return Stream.of(
                    Arguments.of(1, "1로 설정"),
                    Arguments.of(20, "20으로 설정")
            );
        }

        @DisplayName("정상적인 pageable size를 입력했을 때, 해당 size로 반환한다.")
        @MethodSource("provideValidPageableSize")
        @ParameterizedTest(name = "{index} : {1} 인 정상적인 값이 주어졌을 때")
        void getTags_successWithValidPageableSize(int size, String description) throws Exception {
            // given
            var validTag = TagDtoTestDataFactory.createTagResponseDto();
            var pagedTag = createPageObject(List.of(validTag), 0, size, "id", Sort.Direction.DESC);

            doReturn(pagedTag).when(tagService).getAll(PageRequest.of(0, size, Sort.Direction.DESC, "id"));
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tag?size=" + size));

            // then
            result
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.tags[0].id").value(validTag.getId()))
                    .andExpect(jsonPath("$._embedded.tags[0].name").value(validTag.getName()))
                    .andDo(print());

            verify(tagService).getAll(PageRequest.of(0, size, Sort.Direction.DESC, "id"));
        }

        private static Stream<Arguments> provideInvalidPageableSize() {
            return Stream.of(
                    Arguments.of(21, "21로 설정")
            );
        }

        @DisplayName("잘못된 pageable size를 입력했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidPageableSize")
        @ParameterizedTest(name = "{index} : {1} 인 잘못된 값이 주어졌을 때")
        void getTags_failWithInvalidPageableSize(int size, String description) throws Exception {
            // given

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tag?size=" + size));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
            verifyNoInteractions(tagService);
        }

    }

    @DisplayName("getTagById")
    @Nested
    class getTagById {

        @DisplayName("정상적인 ID를 입력했을 때, 해당 ID에 대한 태그를 반환한다.")
        @Test
        void getTagById_success() throws Exception {
            // given
            var validTag = TagDtoTestDataFactory.createTagResponseDto();

            doReturn(validTag).when(tagService).getById(validTag.getId());
            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/tag/{tagId}", validTag.getId()));

            // then
            result
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(validTag.getId()))
                    .andExpect(jsonPath("$.name").value(validTag.getName()))
                    .andDo(print())
                    .andDo(document("tag/getTagById",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Tag API")
                                    .summary("주어진 Id로 태그를 조회합니다.")
                                    .pathParameters(
                                            parameterWithName("tagId").description("조회할 태그 ID")
                                    )
                                    .responseFields(
                                            fieldWithPath("id").type(NUMBER).description("태그 ID"),
                                            fieldWithPath("name").type(SimpleType.STRING).description("태그 이름"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .responseSchema(Schema.schema("Tag.Response"))
                                    .build())));

            verify(tagService).getById(validTag.getId());
        }

        private static Stream<Arguments> provideInvalidIds() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT에서 나올 수 없는 작은 ID"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 최대 값을 넘는  ID")
            );
        }

        @DisplayName("잘못된 ID를 입력했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidIds")
        @ParameterizedTest(name = "{index} : {1} 인 잘못된 값이 주어졌을 때")
        void getTagById_failWithInvalidId(Long id, String description) throws Exception {
            // given

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tag/" + id));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
            verifyNoInteractions(tagService);
        }

    }

    @DisplayName("createTag")
    @Nested
    class createTag {

        @DisplayName("정상적인 요청을 했을 때, 태그를 생성한다.")
        @Test
        void createTag_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var request = TagCreateRequestControllerDto.builder()
                    .name("tag")
                    .build();

            var serviceRequest = TagCreateRequestServiceDto.builder()
                    .name(request.getName())
                    .build();

            var createdTag = TagDtoTestDataFactory.createTagResponseDto();
            createdTag.setName(request.getName());

            doReturn(createdTag).when(tagService).create(serviceRequest);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/tag")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(request)));

            // then
            result
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(createdTag.getId()))
                    .andExpect(jsonPath("$.name").value(createdTag.getName()))
                    .andDo(print())
                    .andDo(document("tag/createTag",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Tag API")
                                    .summary("새로운 태그를 생성합니다. (AUTH_LEVEL: ADMIN)")
                                    .requestFields(
                                            fieldWithPath("name").type(SimpleType.STRING).description("태그 이름 (2~100자)")
                                    )
                                    .responseFields(
                                            fieldWithPath("id").type(NUMBER).description("태그 ID"),
                                            fieldWithPath("name").type(SimpleType.STRING).description("태그 이름"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .requestSchema(Schema.schema("Tag.CreateRequest"))
                                    .responseSchema(Schema.schema("Tag.Response"))
                                    .build())));

            verify(tagService).create(serviceRequest);
        }

        private static Stream<Arguments> provideInvalidRequests() {
            return Stream.of(
                    Arguments.of(TagCreateRequestControllerDto.builder().name("w" .repeat(101)).build(), "20자 보다 긴 name")
            );
        }

        @DisplayName("잘못된 요청을 했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidRequests")
        @ParameterizedTest(name = "{index} : {1} 인 잘못된 값이 주어졌을 때")
        void createTag_failWithInvalidRequest(TagCreateRequestControllerDto request, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tag")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(request)));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
            verifyNoInteractions(tagService);
        }

        @DisplayName("권한이 없는 사용자가 요청했을 때, 403 Forbidden을 반환한다.")
        @Test
        void createTag_failWithNoPermission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            var request = TagCreateRequestControllerDto.builder()
                    .name("tag")
                    .build();

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tag")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(request)));

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
            verifyNoInteractions(tagService);
        }

    }

    @DisplayName("updateTag")
    @Nested
    class updateTag {

        @DisplayName("정상적인 요청을 했을 때, 태그를 수정한다.")
        @Test
        void updateTag_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var id = 1L;
            var request = TagUpdateRequestControllerDto.builder()
                    .name("tag")
                    .build();

            var serviceRequest = TagUpdateRequestServiceDto.builder()
                    .id(id)
                    .name(request.getName())
                    .build();

            var updatedTag = TagDtoTestDataFactory.createTagResponseDto();
            updatedTag.setName(request.getName());

            doReturn(updatedTag).when(tagService).update(serviceRequest);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/tag/{id}", id)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(request)));

            // then
            result
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(updatedTag.getId()))
                    .andExpect(jsonPath("$.name").value(updatedTag.getName()))
                    .andDo(print())
                    .andDo(document("tag/updateTag",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Tag API")
                                    .summary("주어진 Id로 태그를 수정합니다. (AUTH_LEVEL: ADMIN)")
                                    .pathParameters(
                                            parameterWithName("id").description("수정할 태그 ID")
                                    )
                                    .requestFields(
                                            fieldWithPath("name").type(SimpleType.STRING).description("태그 이름 (2~100자)")
                                    )
                                    .responseFields(
                                            fieldWithPath("id").type(NUMBER).description("태그 ID"),
                                            fieldWithPath("name").type(SimpleType.STRING).description("태그 이름"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .requestSchema(Schema.schema("Tag.UpdateRequest"))
                                    .responseSchema(Schema.schema("Tag.Response"))
                                    .build())));

            verify(tagService).update(serviceRequest);
        }

        private static Stream<Arguments> provideInvalidIds() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT에서 나올 수 없는 작은 ID"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 최대 값을 넘는  ID")
            );
        }

        @DisplayName("잘못된 ID를 입력했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidIds")
        @ParameterizedTest(name = "{index} : {1} 인 잘못된 값이 주어졌을 때")
        void updateTag_failWithInvalidId(Long id, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var request = TagUpdateRequestControllerDto.builder()
                    .name("tag")
                    .build();

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tag/{id}", id)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(request)));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());

            verifyNoInteractions(tagService);
        }

        private static Stream<Arguments> provideInvalidRequests() {
            return Stream.of(
                    Arguments.of(TagCreateRequestControllerDto.builder().name("w" .repeat(101)).build(), "20자 보다 긴 name")
            );
        }

        @DisplayName("잘못된 요청을 했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidRequests")
        @ParameterizedTest(name = "{index} : {1} 인 잘못된 값이 주어졌을 때")
        void updateTag_failWithInvalidRequest(TagCreateRequestControllerDto request, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tag/{id}", 1L)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(request)));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());

            verifyNoInteractions(tagService);
        }

        @DisplayName("권한이 없는 사용자가 요청했을 때, 403 Forbidden을 반환한다.")
        @Test
        void updateTag_failWithNoPermission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            var request = TagCreateRequestControllerDto.builder()
                    .name("tag")
                    .build();

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tag/{id}", 1L)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(request)));

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
            verifyNoInteractions(tagService);
        }
    }

    @DisplayName("deleteTag")
    @Nested
    class deleteTag {

        @DisplayName("정상적인 ID를 입력했을 때, 해당 ID에 대한 태그를 삭제한다.")
        @Test
        void deleteTag_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var id = 1L;

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/tag/{id}", id));

            // then
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("tag/deleteTag",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Tag API")
                                    .summary("주어진 Id로 태그를 삭제합니다. (AUTH_LEVEL: ADMIN)")
                                    .pathParameters(
                                            parameterWithName("id").description("삭제할 태그 ID")
                                    )
                                    .build())));

            verify(tagService).delete(id);
        }

        private static Stream<Arguments> provideInvalidIds() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT에서 나올 수 없는 작은 ID"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 최대 값을 넘는  ID")
            );
        }

        @DisplayName("잘못된 ID를 입력했을 때, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidIds")
        @ParameterizedTest(name = "{index} : {1} 인 잘못된 값이 주어졌을 때")
        void deleteTag_failWithInvalidId(Long id, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tag/{id}", id));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());

            verifyNoInteractions(tagService);
        }

        @DisplayName("권한이 없는 사용자가 요청했을 때, 403 Forbidden을 반환한다.")
        @Test
        void deleteTag_failWithNoPermission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tag/{id}", 1L));

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
            verifyNoInteractions(tagService);
        }

    }
}
