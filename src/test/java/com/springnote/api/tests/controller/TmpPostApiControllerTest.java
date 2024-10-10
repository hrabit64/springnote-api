package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.dto.assembler.post.PostDetailResponseCommonDtoAssembler;
import com.springnote.api.dto.assembler.tmpPost.TmpPostResponseCommonDtoAssembler;
import com.springnote.api.dto.general.common.PostTagId;
import com.springnote.api.dto.tmpPost.controller.TmpPostCreateRequestControllerDto;
import com.springnote.api.dto.tmpPost.controller.TmpPostUpdateRequestControllerDto;
import com.springnote.api.dto.tmpPost.service.TmpPostCreateRequestServiceDto;
import com.springnote.api.dto.tmpPost.service.TmpPostUpdateRequestServiceDto;
import com.springnote.api.service.TmpPostService;
import com.springnote.api.testUtils.dataFactory.post.PostDtoTestDataFactory;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.web.controller.TmpPostApiController;
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
import org.springframework.data.domain.Sort;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createPageObject;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createUserContextReturns;
import static com.springnote.api.testUtils.dataFactory.tmpPost.TmpPostDtoDataFactory.createMatcher;
import static com.springnote.api.testUtils.dataFactory.tmpPost.TmpPostDtoDataFactory.createTmpPostResponseCommonDto;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller Test - TmpPostApiController")
@WebMvcTest(TmpPostApiController.class)
public class TmpPostApiControllerTest extends ControllerTestTemplate {

    @Autowired
    private TmpPostApiController tmpPostApiController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TmpPostService tmpPostService;

    @SpyBean
    private TmpPostResponseCommonDtoAssembler tmpPostAssembler;

    @SpyBean
    private PostDetailResponseCommonDtoAssembler postDetailAssembler;

    @DisplayName("createTmpPost")
    @Nested
    class createTmpPost {

        @DisplayName("올바른 요청이 들어오면 생성된 TmpPostResponseCommonDto를 반환한다.")
        @Test
        void createTmpPost_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validRequest = TmpPostCreateRequestControllerDto.builder()
                    .postTypeId(1L)
                    .build();

            var serviceDto = TmpPostCreateRequestServiceDto.builder()
                    .postTypeId(1L)
                    .tagIds(List.of())
                    .build();

            var savedTmpPost = createTmpPostResponseCommonDto();

            doReturn(savedTmpPost).when(tmpPostService).create(serviceDto);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/tmp-post")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest))
            );

            // then
            result.andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("tmpPost/createTmpPost",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("TmpPost API")
                                    .summary("임시 포스트를 게시합니다. (AUTH_LEVEL: ADMIN)")
                                    .requestFields(
                                            fieldWithPath("post_type_id").description("포스트 타입 ID"),
                                            fieldWithPath("series_id").description("시리즈 ID"),
                                            fieldWithPath("tag_ids").description("태그 ID 목록"),
                                            fieldWithPath("content").description("본문 30000자 이내"),
                                            fieldWithPath("title").description("제목 300자 이내"),
                                            fieldWithPath("thumbnail").description("썸네일 주소")
                                    )
                                    .responseFields(
                                            subsectionWithPath("_links").ignored(),
                                            fieldWithPath("id").type(STRING).description("대상 포스트 ID"),
                                            fieldWithPath("title").type(STRING).description("포스트 제목"),
                                            fieldWithPath("created_at").type(STRING).description("포스트 생성일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("last_updated_at").type(STRING).description("포스트 최종 수정일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("post_type.id").type(NUMBER).description("포스트 타입 ID"),
                                            fieldWithPath("post_type.name").type(STRING).description("포스트 타입 이름"),
                                            fieldWithPath("series.id").type(NUMBER).description("시리즈 ID"),
                                            fieldWithPath("series.name").type(STRING).description("시리즈 이름"),
                                            fieldWithPath("thumbnail").type(STRING).description("썸네일 이미지 URL").optional(),
                                            fieldWithPath("tags[].id").type(NUMBER).description("태그 ID"),
                                            fieldWithPath("tags[].name").type(STRING).description("태그 이름"),
                                            fieldWithPath("content").description("본문")

                                    )
                                    .requestSchema(Schema.schema("TmpPost.CreateRequest"))
                                    .responseSchema(Schema.schema("TmpPost.Response"))
                                    .build())));

            createMatcher(savedTmpPost, result, false);
        }

        private static Stream<Arguments> provideInvalidRequest() {
            var tooLongTitle = TmpPostCreateRequestControllerDto
                    .builder()
                    .title("a".repeat(301))
                    .postTypeId(1L)
                    .build();

            var tooLongContent = TmpPostCreateRequestControllerDto
                    .builder()
                    .content("a".repeat(30001))
                    .postTypeId(1L)
                    .build();

            var invalidPostTypeId = TmpPostCreateRequestControllerDto
                    .builder()
                    .postTypeId(0L)
                    .build();

            var tooLargePostTypeId = TmpPostCreateRequestControllerDto
                    .builder()
                    .postTypeId(DBTypeSize.TINYINT + 1)
                    .build();

            var noPostTypeId = TmpPostCreateRequestControllerDto
                    .builder()
                    .build();

            var tooManyTags = TmpPostCreateRequestControllerDto
                    .builder()
                    .tagIds(List.of(
                            new PostTagId(1L),
                            new PostTagId(2L),
                            new PostTagId(3L),
                            new PostTagId(4L),
                            new PostTagId(5L),
                            new PostTagId(6L),
                            new PostTagId(7L),
                            new PostTagId(8L),
                            new PostTagId(9L),
                            new PostTagId(10L),
                            new PostTagId(11L)
                    ))
                    .postTypeId(1L)
                    .build();

            var invalidThumbnail = TmpPostCreateRequestControllerDto
                    .builder()
                    .thumbnail("invalid")
                    .postTypeId(1L)
                    .build();

            return Stream.of(
                    Arguments.of(tooLongTitle, "300자가 넘는 제목"),
                    Arguments.of(tooLongContent, "30000자가 넘는 본문"),
                    Arguments.of(invalidPostTypeId, "0인 postTypeId"),
                    Arguments.of(tooLargePostTypeId, "TINYINT의 최대 값보다 큰 postTypeId"),
                    Arguments.of(noPostTypeId, "postTypeId가 없는 요청"),
                    Arguments.of(tooManyTags, "태그가 10개 초과인 요청"),
                    Arguments.of(invalidThumbnail, "유효하지 않은 썸네일 주소")
            );
        }

        @DisplayName("올바르지 않은 요청이 들어오면 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidRequest")
        @ParameterizedTest(name = "{index} : {1}이(가) 주어졌을 때")
        void createTmpPost_invalidRequest(TmpPostCreateRequestControllerDto invalidRequest, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tmp-post")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(invalidRequest))
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER 권한"),
                    Arguments.of(AuthLevel.NONE, "인증되지 않은 사용자")
            );
        }

        @DisplayName("관리자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}가 요청했을 때")
        void createTmpPost_invalidAuthLevel(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            var validRequest = TmpPostCreateRequestControllerDto.builder()
                    .postTypeId(1L)
                    .build();

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tmp-post")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest))
            );

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

    @DisplayName("getTmpPostById")
    @Nested
    class getTmpPostById {

        @DisplayName("올바른 ID가 들어오면 해당 TmpPostResponseCommonDto를 반환한다.")
        @Test
        void getTmpPostById_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var id = UUID.randomUUID().toString();
            var tmpPost = createTmpPostResponseCommonDto();

            doReturn(tmpPost).when(tmpPostService).getById(id);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/tmp-post/{tmpPostId}", id));

            // then
            result.andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("tmpPost/getTmpPostById",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Series API")
                                    .summary("임시 포스트를 조회합니다. (AUTH_LEVEL: ADMIN)")
                                    .pathParameters(
                                            parameterWithName("tmpPostId").description("임시 포스트 ID")
                                    )
                                    .responseFields(
                                            subsectionWithPath("_links").ignored(),
                                            fieldWithPath("id").type(STRING).description("대상 포스트 ID"),
                                            fieldWithPath("title").type(STRING).description("포스트 제목"),
                                            fieldWithPath("created_at").type(STRING).description("포스트 생성일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("last_updated_at").type(STRING).description("포스트 최종 수정일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("post_type.id").type(NUMBER).description("포스트 타입 ID"),
                                            fieldWithPath("post_type.name").type(STRING).description("포스트 타입 이름"),
                                            fieldWithPath("series.id").type(NUMBER).description("시리즈 ID"),
                                            fieldWithPath("series.name").type(STRING).description("시리즈 이름"),
                                            fieldWithPath("thumbnail").type(STRING).description("썸네일 이미지 URL").optional(),
                                            fieldWithPath("tags[].id").type(NUMBER).description("태그 ID"),
                                            fieldWithPath("tags[].name").type(STRING).description("태그 이름"),
                                            fieldWithPath("content").description("본문")
                                    )
                                    .responseSchema(Schema.schema("TmpPost.Response"))
                                    .build())));

            createMatcher(tmpPost, result, false);
        }

        private static Stream<Arguments> provideInvalidRequest() {
            return Stream.of(
                    Arguments.of("123", "UUID가 아닌 ID")
            );
        }

        @DisplayName("올바르지 않은 요청이 들어오면 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidRequest")
        @ParameterizedTest(name = "{index} : {1}이(가) 주어졌을 때")
        void getTmpPostById_invalidRequest(String id, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tmp-post/" + id));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER 권한"),
                    Arguments.of(AuthLevel.NONE, "인증되지 않은 사용자")
            );
        }

        @DisplayName("관리자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}가 요청했을 때")
        void getTmpPostById_invalidAuthLevel(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            var id = UUID.randomUUID().toString();

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tmp-post/" + id));

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

    @DisplayName("getTmpPostList")
    @Nested
    class getTmpPostList {

        @DisplayName("올바른 요청이 들어오면 TmpPostResponseCommonDto 목록을 반환한다.")
        @Test
        void getTmpPostList_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var tmpPost = createTmpPostResponseCommonDto();
            var tmpPostList = createPageObject(List.of(tmpPost), 0, 20, "createdDate", Sort.Direction.DESC);

            doReturn(tmpPostList).when(tmpPostService).getAll(tmpPostList.getPageable());

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/tmp-post"));

            // then
            result.andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("tmpPost/getTmpPostList",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("TmpPost API")
                                    .summary("임시 포스트 목록을 조회합니다. (AUTH_LEVEL: ADMIN)")
                                    .description(
                                            "* 페이징\n" +
                                                    "\n" +
                                                    "해당 API는 페이징을 지원합니다.\n" +
                                                    "\n" +
                                                    "|  키  |          설명           |            제약            |      기본값      |\n" +
                                                    "| :--: | :---------------------: | :------------------------: | :--------------: |\n" +
                                                    "| size |      페이징 사이즈      |        최대 50까지        |        20        |\n" +
                                                    "| page | 페이지 넘버 *0부터 시작 |                            |        0         |\n" +
                                                    "| sort |        정렬 옵션        | *아래 사용 가능 키만 가능* | createdDate;DESC |\n" +
                                                    "\n" +
                                                    "사용 가능한 sort키는 아래와 같습니다. *모든 Sort 사용시 방향을 지정하지 않으면 ASC 로 동작합니다. (대소문자 구분 없음)*\n" +
                                                    "\n" +
                                                    "|        키        |     설명      |\n" +
                                                    "| :--------------: | :-----------: |\n" +
                                                    "| lastModifiedDate | 마지막 수정일 |\n" +
                                                    "|   createdDate    |    생성일     |\n" +
                                                    "\n"
                                    )
                                    .responseFields(
                                            subsectionWithPath("_links").ignored(),
                                            subsectionWithPath("_embedded.tmp_posts[]._links").ignored(),
                                            fieldWithPath("page.size").type(NUMBER).description("페이지 크기"),
                                            fieldWithPath("page.totalElements").type(NUMBER).description("전체 요소 수"),
                                            fieldWithPath("page.totalPages").type(NUMBER).description("전체 페이지 수"),
                                            fieldWithPath("page.number").type(NUMBER).description("현재 페이지 번호"),
                                            fieldWithPath("_embedded.tmp_posts[].id").type(STRING).description("대상 포스트 ID"),
                                            fieldWithPath("_embedded.tmp_posts[].title").type(STRING).description("포스트 제목"),
                                            fieldWithPath("_embedded.tmp_posts[].created_at").type(STRING).description("포스트 생성일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("_embedded.tmp_posts[].last_updated_at").type(STRING).description("포스트 최종 수정일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("_embedded.tmp_posts[].post_type.id").type(NUMBER).description("포스트 타입 ID"),
                                            fieldWithPath("_embedded.tmp_posts[].post_type.name").type(STRING).description("포스트 타입 이름"),
                                            fieldWithPath("_embedded.tmp_posts[].series.id").type(NUMBER).description("시리즈 ID"),
                                            fieldWithPath("_embedded.tmp_posts[].series.name").type(STRING).description("시리즈 이름"),
                                            fieldWithPath("_embedded.tmp_posts[].thumbnail").type(STRING).description("썸네일 이미지 URL").optional(),
                                            fieldWithPath("_embedded.tmp_posts[].tags[].id").type(NUMBER).description("태그 ID"),
                                            fieldWithPath("_embedded.tmp_posts[].tags[].name").type(STRING).description("태그 이름"),
                                            fieldWithPath("_embedded.tmp_posts[].content").type(STRING).description("포스트 본문")).build()

                            )));
            createMatcher(tmpPost, result, true);

        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER 권한"),
                    Arguments.of(AuthLevel.NONE, "인증되지 않은 사용자")
            );
        }

        @DisplayName("관리자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}가 요청했을 때")
        void getTmpPostList_invalidAuthLevel(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tmp-post"));

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
        }

        private static Stream<Arguments> provideValidSortKey() {
            return Stream.of(
                    Arguments.of("createdDate"),
                    Arguments.of("lastModifiedDate")
            );
        }

        @DisplayName("올바른 SortKey가 들어오면 해당 TmpPostResponseCommonDto 목록을 반환한다.")
        @MethodSource("provideValidSortKey")
        @ParameterizedTest(name = "{index} : {0}가 주어졌을 때")
        void getTmpPostList_validSortKey(String sortKey) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var tmpPost = createTmpPostResponseCommonDto();
            var tmpPostList = createPageObject(List.of(tmpPost), 0, 20, sortKey, Sort.Direction.ASC);

            doReturn(tmpPostList).when(tmpPostService).getAll(tmpPostList.getPageable());

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tmp-post")
                    .param("sort", sortKey)
            );

            // then
            result.andExpect(status().isOk())
                    .andDo(print());

            createMatcher(tmpPost, result, true);
        }

        @DisplayName("올바르지 않은 SortKey가 들어오면 400 Bad Request를 반환한다.")
        @Test
        void getTmpPostList_invalidSortKey() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var invalidSortKey = "invalid";
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tmp-post")
                    .param("sort", invalidSortKey)
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        @DisplayName("올바르지 않은 size가 들어오면 400 Bad Request를 반환한다.")
        @Test
        void getTmpPostList_invalidSize() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var invalidSize = 51;
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/tmp-post")
                    .param("size", String.valueOf(invalidSize))
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @DisplayName("updateTmpPost")
    @Nested
    class updateTmpPost {

        @DisplayName("올바른 요청이 들어오면 수정된 TmpPostResponseCommonDto를 반환한다.")
        @Test
        void updateTmpPost_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var id = UUID.randomUUID().toString();
            var validRequest = TmpPostUpdateRequestControllerDto.builder()
                    .build();

            var serviceDto = TmpPostUpdateRequestServiceDto.builder()
                    .id(id)
                    .tagIds(List.of())
                    .build();

            var updatedTmpPost = createTmpPostResponseCommonDto();

            doReturn(updatedTmpPost).when(tmpPostService).update(serviceDto);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/tmp-post/{tmpPostId}", id)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest))
            );

            // then
            result.andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("tmpPost/updateTmpPost",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("TmpPost API")
                                    .summary("임시 포스트를 수정합니다. (AUTH_LEVEL: ADMIN)")
                                    .pathParameters(
                                            parameterWithName("tmpPostId").description("임시 포스트 ID")
                                    )
                                    .requestFields(
                                            fieldWithPath("series_id").description("시리즈 ID").optional(),
                                            fieldWithPath("tag_ids").description("태그 ID 목록").optional(),
                                            fieldWithPath("content").description("본문 30000자 이내"),
                                            fieldWithPath("title").description("제목 300자 이내"),
                                            fieldWithPath("thumbnail").description("썸네일 주소")
                                    )
                                    .responseFields(
                                            subsectionWithPath("_links").ignored(),
                                            fieldWithPath("id").type(STRING).description("대상 포스트 ID"),
                                            fieldWithPath("title").type(STRING).description("포스트 제목"),
                                            fieldWithPath("created_at").type(STRING).description("포스트 생성일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("last_updated_at").type(STRING).description("포스트 최종 수정일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("post_type.id").type(NUMBER).description("포스트 타입 ID"),
                                            fieldWithPath("post_type.name").type(STRING).description("포스트 타입 이름"),
                                            fieldWithPath("series.id").type(NUMBER).description("시리즈 ID"),
                                            fieldWithPath("series.name").type(STRING).description("시리즈 이름"),
                                            fieldWithPath("thumbnail").type(STRING).description("썸네일 이미지 URL").optional(),
                                            fieldWithPath("tags[].id").type(NUMBER).description("태그 ID"),
                                            fieldWithPath("tags[].name").type(STRING).description("태그 이름"),
                                            fieldWithPath("content").description("본문"))
                                    .requestSchema(Schema.schema("TmpPost.UpdateRequest"))
                                    .responseSchema(Schema.schema("TmpPost.Response"))
                                    .build())));

            createMatcher(updatedTmpPost, result, false);
        }

        private static Stream<Arguments> provideInvalidRequest() {
            var tooLongTitle = TmpPostUpdateRequestControllerDto
                    .builder()
                    .title("a".repeat(301))
                    .build();

            var tooLongContent = TmpPostUpdateRequestControllerDto
                    .builder()
                    .content("a".repeat(30001))
                    .build();

            var tooManyTags = TmpPostUpdateRequestControllerDto
                    .builder()
                    .tagIds(List.of(
                            new PostTagId(1L),
                            new PostTagId(2L),
                            new PostTagId(3L),
                            new PostTagId(4L),
                            new PostTagId(5L),
                            new PostTagId(6L),
                            new PostTagId(7L),
                            new PostTagId(8L),
                            new PostTagId(9L),
                            new PostTagId(10L),
                            new PostTagId(11L)
                    ))
                    .build();

            var invalidThumbnail = TmpPostUpdateRequestControllerDto
                    .builder()
                    .thumbnail("invalid")
                    .build();

            return Stream.of(
                    Arguments.of(tooLongTitle, "300자가 넘는 제목"),
                    Arguments.of(tooLongContent, "30000자가 넘는 본문"),
                    Arguments.of(tooManyTags, "태그가 10개 초과인 요청"),
                    Arguments.of(invalidThumbnail, "유효하지 않은 썸네일 주소")
            );
        }

        @DisplayName("올바르지 않은 요청이 들어오면 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidRequest")
        @ParameterizedTest(name = "{index} : {1}이(가) 주어졌을 때")
        void updateTmpPost_invalidRequest(TmpPostUpdateRequestControllerDto invalidRequest, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var id = UUID.randomUUID().toString();
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tmp-post/{id}", id)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(invalidRequest))
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER 권한"),
                    Arguments.of(AuthLevel.NONE, "인증되지 않은 사용자")
            );
        }

        @DisplayName("관리자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}가 요청했을 때")
        void updateTmpPost_invalidAuthLevel(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);
            var id = UUID.randomUUID().toString();
            var validRequest = TmpPostUpdateRequestControllerDto.builder()
                    .build();
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tmp-post/{id}", id)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest))
            );

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidId() {
            return Stream.of(
                    Arguments.of("123", "UUID가 아닌 ID")
            );
        }

        @DisplayName("올바르지 않은 ID가 들어오면 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidId")
        @ParameterizedTest(name = "{index} : {1}이(가) 주어졌을 때")
        void updateTmpPost_invalidId(String id, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var validRequest = TmpPostUpdateRequestControllerDto.builder()
                    .build();
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/tmp-post/{id}", id)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest))
            );

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }
    }

    @DisplayName("deleteTmpPost")
    @Nested
    class deleteTmpPost {

        @DisplayName("올바른 ID가 들어오면 204 No Content를 반환한다.")
        @Test
        void deleteTmpPost_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var id = UUID.randomUUID().toString();

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/tmp-post/{tmpPostId}", id));

            // then
            result.andExpect(status().isNoContent())
                    .andDo(print())
                    .andDo(document("tmpPost/deleteTmpPost",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("TmpPost API")
                                    .summary("임시 포스트를 삭제합니다. (AUTH_LEVEL: ADMIN)")
                                    .pathParameters(
                                            parameterWithName("tmpPostId").description("임시 포스트 ID")
                                    )
                                    .build())));
        }

        private static Stream<Arguments> provideInvalidRequest() {
            return Stream.of(
                    Arguments.of("123", "UUID가 아닌 ID"),
                    Arguments.of(null, "null ID")
            );
        }

        @DisplayName("올바르지 않은 요청이 들어오면 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidRequest")
        @ParameterizedTest(name = "{index} : {1}이(가) 주어졌을 때")
        void deleteTmpPost_invalidRequest(String id, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tmp-post/" + id));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER 권한"),
                    Arguments.of(AuthLevel.NONE, "인증되지 않은 사용자")
            );
        }

        @DisplayName("관리자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}가 요청했을 때")
        void deleteTmpPost_invalidAuthLevel(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            var id = UUID.randomUUID().toString();

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/tmp-post/" + id));

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
        }
    }

    @DisplayName("publishTmpPost")
    @Nested
    class publishTmpPost {

        @DisplayName("올바른 ID가 들어오면 생성된 Post를 반환한다.")
        @Test
        void publishTmpPost_success() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var id = UUID.randomUUID().toString();

            var createdPost = PostDtoTestDataFactory.createPostDetailResponseCommonDto();

            doReturn(createdPost).when(tmpPostService).convertToPost(id);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/tmp-post/{tmpPostId}/publish", id));

            // then
            result.andExpect(status().isOk())
                    .andDo(print())
                    .andDo(document("tmpPost/publishTmpPost",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("TmpPost API")
                                    .summary("임시 포스트를 게시합니다. (AUTH_LEVEL: ADMIN)")
                                    .pathParameters(
                                            parameterWithName("tmpPostId").description("임시 포스트 ID")
                                    )
                                    .responseFields(
                                            subsectionWithPath("_links").ignored(),
                                            fieldWithPath("id").type(NUMBER).description("대상 포스트 ID"),
                                            fieldWithPath("title").type(STRING).description("포스트 제목"),
                                            fieldWithPath("content.editor_text").type(STRING).description("포스트 본문 - 에디터 텍스트(마크다운)"),
                                            fieldWithPath("content.plain_text").type(STRING).description("포스트 본문 - 일반 텍스트(순수 텍스트만)"),
                                            fieldWithPath("created_at").type(STRING).description("포스트 생성일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("last_updated_at").type(STRING).description("포스트 최종 수정일 (yyyy-MM-dd`T`HH:mm:ss)"),
                                            fieldWithPath("post_type.id").type(NUMBER).description("포스트 타입 ID"),
                                            fieldWithPath("post_type.name").type(STRING).description("포스트 타입 이름"),
                                            fieldWithPath("enabled").type(BOOLEAN).description("포스트 공개 여부 (0: 비공개, 1: 공개)"),
                                            fieldWithPath("series.id").type(NUMBER).description("시리즈 ID"),
                                            fieldWithPath("series.name").type(STRING).description("시리즈 이름"),
                                            fieldWithPath("thumbnail").type(STRING).description("썸네일 이미지 URL").optional(),
                                            fieldWithPath("tags[].id").type(NUMBER).description("태그 ID"),
                                            fieldWithPath("tags[].name").type(STRING).description("태그 이름")
                                    )
                                    .responseSchema(Schema.schema("Post.Response"))
                                    .build())));

            PostDtoTestDataFactory.createMatcher(createdPost, result, false);
        }

        private static Stream<Arguments> provideInvalidRequest() {
            return Stream.of(
                    Arguments.of("123", "UUID가 아닌 ID"),
                    Arguments.of(null, "null ID")
            );
        }

        @DisplayName("올바르지 않은 요청이 들어오면 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidRequest")
        @ParameterizedTest(name = "{index} : {1}이(가) 주어졌을 때")
        void publishTmpPost_invalidRequest(String id, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tmp-post/" + id + "/publish"));

            // then
            result.andExpect(status().isBadRequest())
                    .andDo(print());
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER 권한"),
                    Arguments.of(AuthLevel.NONE, "인증되지 않은 사용자")
            );
        }

        @DisplayName("관리자가 아닌 유저가 요청하면 403 Forbidden을 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}가 요청했을 때")
        void publishTmpPost_invalidAuthLevel(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            var id = UUID.randomUUID().toString();

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/tmp-post/" + id + "/publish"));

            // then
            result.andExpect(status().isForbidden())
                    .andDo(print());
        }
    }
}
