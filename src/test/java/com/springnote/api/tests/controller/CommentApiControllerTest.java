package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.config.CommentConfig;
import com.springnote.api.dto.assembler.comment.CommentResponseCommonDtoAssembler;
import com.springnote.api.dto.assembler.comment.CommentResponseWithReplyCntCommonDtoAssembler;
import com.springnote.api.dto.assembler.comment.ReplyResponseCommonDtoAssembler;
import com.springnote.api.dto.comment.controller.CommentCreateRequestControllerDto;
import com.springnote.api.dto.comment.controller.CommentUpdateRequestControllerDto;
import com.springnote.api.dto.comment.controller.ReplyCreateRequestControllerDto;
import com.springnote.api.dto.comment.service.CommentCreateRequestServiceDto;
import com.springnote.api.dto.comment.service.CommentUpdateRequestServiceDto;
import com.springnote.api.dto.comment.service.ReplyCreateRequestServiceDto;
import com.springnote.api.security.captcha.CaptchaManager;
import com.springnote.api.service.CommentService;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.utils.context.RequestContext;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.web.controller.CommentApiController;
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
import java.util.stream.Stream;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createPageObject;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createUserContextReturns;
import static com.springnote.api.testUtils.dataFactory.comment.CommentDtoTestDataFactory.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller Test - CommentApiController")
@WebMvcTest(CommentApiController.class)
public class CommentApiControllerTest extends ControllerTestTemplate {

    @Autowired
    private CommentApiController commentApiController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CommentService commentService;

    @SpyBean
    private CommentResponseCommonDtoAssembler commentAssembler;

    @SpyBean
    private ReplyResponseCommonDtoAssembler replyAssembler;

    @MockBean
    private CaptchaManager captchaManager;

    @SpyBean
    private CommentResponseWithReplyCntCommonDtoAssembler commentWithReplyCntAssembler;

    @MockBean
    private RequestContext requestContext;

    @MockBean
    private CommentConfig commentConfig;

    @DisplayName("getCommentsWithPost")
    @Nested
    class getCommentsWithPost {

        @DisplayName("올바른 Post Id가 주어지면, 해당 Post에 대한 Comment 목록을 반환한다.")
        @Test
        void givenValidPostId_whenGetCommentsWithPost_thenReturnsCommentList() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(true).when(commentConfig).isViewEnable();

            var validPostId = 1L;

            var validComment = createCommentResponseWithReplyCntCommonDto();
            var pagedComment = createPageObject(List.of(validComment), 0, 20, "createdDate", Sort.Direction.DESC);

            doReturn(pagedComment).when(commentService).getParentCommentWithPost(validPostId, pagedComment.getPageable());

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v1/post/{postId}/comment", validPostId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(
                            document("Comment/getCommentsWithPost",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(ResourceSnippetParameters.builder()
                                            .tag("Comment API")
                                            .summary("해당 포스트에 대한 댓글 목록을 반환합니다.(AUTH_LEVEL: NONE)")
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
                                                            "|        id        |    댓글 id    |\n" +
                                                            "|       user       |    작성자     |\n" +
                                                            "|   createdDate    |    생성일     |\n" +
                                                            "| lastModifiedDate | 마지막 수정일 |" +
                                                            "\n"
                                            )
                                            .pathParameters(
                                                    parameterWithName("postId").description("포스트 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("_embedded.comments[].content").type(STRING).description("댓글 본문"),
                                                    fieldWithPath("_embedded.comments[].created_date").type(STRING).description("작성일"),
                                                    fieldWithPath("_embedded.comments[].enabled").type(BOOLEAN).description("활성화 여부"),
                                                    fieldWithPath("_embedded.comments[].id").type(NUMBER).description("댓글 ID"),
                                                    fieldWithPath("_embedded.comments[].last_modified_date").type(STRING).description("수정일"),
                                                    fieldWithPath("_embedded.comments[].post_id").type(NUMBER).description("포스트 ID"),
                                                    fieldWithPath("_embedded.comments[].reply_count").type(NUMBER).description("답글 수"),
                                                    fieldWithPath("_embedded.comments[].writer.id").type(STRING).description("작성자 ID"),
                                                    fieldWithPath("_embedded.comments[].writer.admin").type(BOOLEAN).description("작성자 관리자 여부"),
                                                    fieldWithPath("_embedded.comments[].writer.enabled").type(BOOLEAN).description("작성자 활성화 여부"),
                                                    fieldWithPath("_embedded.comments[].writer.name").type(STRING).description("작성자 이름"),
                                                    fieldWithPath("_embedded.comments[].writer.email").type(STRING).description("작성자 이메일"),
                                                    fieldWithPath("_embedded.comments[].writer.profile_img").type(STRING).description("작성자 프로필 이미지 URL"),
                                                    subsectionWithPath("_embedded.comments[]._links").ignored(),
                                                    fieldWithPath("page.size").type(NUMBER).description("페이지 크기"),
                                                    fieldWithPath("page.totalElements").type(NUMBER).description("전체 요소 수"),
                                                    fieldWithPath("page.totalPages").type(NUMBER).description("전체 페이지 수"),
                                                    fieldWithPath("page.number").type(NUMBER).description("현재 페이지 번호"),
                                                    subsectionWithPath("_links").ignored()
                                            )
                                            .responseSchema(Schema.schema("Comment.PagedResponse"))
                                            .build())
                            ));

            createMatcher(validComment, result, true);
        }

        private static Stream<Arguments> provideInvalidPostId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT에서 나올 수 없는 값"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 타입의 최대값보다 큰 값")
            );
        }

        @DisplayName("올바르지 않은 Post Id가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidPostId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidPostId_whenGetCommentsWithPost_thenReturnsBadRequest(Long invalidPostId, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(true).when(commentConfig).isViewEnable();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/post/{postId}/comment", invalidPostId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("Comment 조회가 비활성화되어 있을 때 일반 유저가 조회하면, 403 Forbidden을 반환한다.")
        @Test
        void givenCommentViewDisabled_whenGetCommentsWithPost_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(false).when(commentConfig).isViewEnable();

            var validPostId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/post/{postId}/comment", validPostId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("Comment 조회가 비활성화되어 있을 때 관리자가 조회하면, 해당 Post에 대한 Comment 목록을 반환한다.")
        @Test
        void givenCommentViewDisabled_whenGetCommentsWithPost_thenReturnsCommentList() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(false).when(commentConfig).isViewEnable();

            var validPostId = 1L;

            var validComment = createCommentResponseWithReplyCntCommonDto();
            var pagedComment = createPageObject(List.of(validComment), 0, 20, "createdDate", Sort.Direction.DESC);

            doReturn(pagedComment).when(commentService).getParentCommentWithPost(validPostId, pagedComment.getPageable());

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/post/{postId}/comment", validPostId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validComment, result, true);
        }

        private static Stream<Arguments> provideValidSortKey() {

            return Stream.of(
                    Arguments.of("id"),
                    Arguments.of("user"),
                    Arguments.of("createdDate"),
                    Arguments.of("lastModifiedDate")
            );
        }

        @DisplayName("올바른 Sort Key가 주어지면, 해당 Key로 Comment 목록을 정렬한다.")
        @MethodSource("provideValidSortKey")
        @ParameterizedTest(name = "{index} : {0} 가 주어졌을 때")
        void givenValidSortKey_whenGetCommentsWithPost_thenReturnsSortedCommentList(String validSortKey) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(true).when(commentConfig).isViewEnable();

            var validPostId = 1L;

            var validComment = createCommentResponseWithReplyCntCommonDto();
            var pagedComment = createPageObject(List.of(validComment), 0, 20, validSortKey, Sort.Direction.ASC);

            doReturn(pagedComment).when(commentService).getParentCommentWithPost(validPostId, pagedComment.getPageable());

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/post/{postId}/comment?sort=" + validSortKey, validPostId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validComment, result, true);
        }

        @DisplayName("올바르지 않은 Sort Key가 주어지면, 400 Bad Request를 반환한다.")
        @Test
        void givenInvalidSortKey_whenGetCommentsWithPost_thenReturnsBadRequest() throws Exception {
            // given
            var notValidSortKey = "notValidSortKey";

            var validPostId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/post/{postId}/comment?sort=" + notValidSortKey, validPostId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("올바르지 않은 Page Size가 주어지면, 400 Bad Request를 반환한다.")
        @Test
        void givenInvalidPageSize_whenGetCommentsWithPost_thenReturnsBadRequest() throws Exception {
            // given
            var invalidPageSize = 51;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/post/{postId}/comment?size=" + invalidPageSize, 1)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAuthLevels() {
            return Stream.of(
                    Arguments.of(AuthLevel.ADMIN, "관리자 권한"),
                    Arguments.of(AuthLevel.USER, "일반 유저 권한"),
                    Arguments.of(AuthLevel.NONE, "인증 없음")
            );
        }

        @DisplayName("댓글 조회가 활성화 되어 있을 때 권한 상관없이 댓글 목록을 반환한다.")
        @MethodSource("provideAuthLevels")
        @ParameterizedTest(name = "{index} : {1} 일 때")
        void givenCommentViewEnabled_whenGetCommentsWithPost_thenReturnsCommentList(AuthLevel authLevel, String message) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);
            doReturn(true).when(commentConfig).isViewEnable();

            var validPostId = 1L;

            var validComment = createCommentResponseWithReplyCntCommonDto();
            var pagedComment = createPageObject(List.of(validComment), 0, 20, "createdDate", Sort.Direction.DESC);

            doReturn(pagedComment).when(commentService).getParentCommentWithPost(validPostId, pagedComment.getPageable());

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/post/{postId}/comment", validPostId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validComment, result, true);
        }
    }

    @DisplayName("getReply")
    @Nested
    class getReply {

        @DisplayName("올바른 Post Id와 Comment Id가 주어지면, 해당 Comment에 대한 Reply 목록을 반환한다.")
        @Test
        void givenValidPostIdAndCommentId_whenGetReply_thenReturnsReplyList() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(true).when(commentConfig).isViewEnable();

            var validCommentId = 1L;

            var validReply = createReplyResponseCommonDto();
            var pagedReply = createPageObject(List.of(validReply), 0, 20, "createdDate", Sort.Direction.DESC);

            doReturn(pagedReply).when(commentService).getReply(validCommentId, pagedReply.getPageable());

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v1/comment/{commentId}/reply", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(
                            document("Comment/getReply",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(ResourceSnippetParameters.builder()
                                            .tag("Comment API")
                                            .summary("해당 댓글에 대한 답글 목록을 반환합니다.(AUTH_LEVEL: NONE)")
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
                                                            "|        id        |    댓글 id    |\n" +
                                                            "|       user       |    작성자     |\n" +
                                                            "|   createdDate    |    생성일     |\n" +
                                                            "| lastModifiedDate | 마지막 수정일 |" +
                                                            "\n"
                                            )
                                            .pathParameters(
                                                    parameterWithName("commentId").description("댓글 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("_embedded.replies[].content").type(STRING).description("댓글 본문"),
                                                    fieldWithPath("_embedded.replies[].created_date").type(STRING).description("작성일"),
                                                    fieldWithPath("_embedded.replies[].enabled").type(BOOLEAN).description("활성화 여부"),
                                                    fieldWithPath("_embedded.replies[].id").type(NUMBER).description("댓글 ID"),
                                                    fieldWithPath("_embedded.replies[].last_modified_date").type(STRING).description("수정일"),
                                                    fieldWithPath("_embedded.replies[].post_id").type(NUMBER).description("포스트 ID"),
                                                    fieldWithPath("_embedded.replies[].parent_id").type(NUMBER).description("부모 댓글 ID"),
                                                    fieldWithPath("_embedded.replies[].writer.id").type(STRING).description("작성자 ID"),
                                                    fieldWithPath("_embedded.replies[].writer.admin").type(BOOLEAN).description("작성자 관리자 여부"),
                                                    fieldWithPath("_embedded.replies[].writer.enabled").type(BOOLEAN).description("작성자 활성화 여부"),
                                                    fieldWithPath("_embedded.replies[].writer.name").type(STRING).description("작성자 이름"),
                                                    fieldWithPath("_embedded.replies[].writer.email").type(STRING).description("작성자 이메일"),
                                                    fieldWithPath("_embedded.replies[].writer.profile_img").type(STRING).description("작성자 프로필 이미지 URL"),
                                                    subsectionWithPath("_embedded.replies[]._links").ignored(),
                                                    fieldWithPath("page.size").type(NUMBER).description("페이지 크기"),
                                                    fieldWithPath("page.totalElements").type(NUMBER).description("전체 요소 수"),
                                                    fieldWithPath("page.totalPages").type(NUMBER).description("전체 페이지 수"),
                                                    fieldWithPath("page.number").type(NUMBER).description("현재 페이지 번호"),
                                                    subsectionWithPath("_links").ignored()
                                            )
                                            .responseSchema(Schema.schema("Comment.PagedResponse"))
                                            .build())
                            ));

            createMatcher(validReply, result, true);
        }

        private static Stream<Arguments> provideInvalidCommentId() {
            return Stream.of(
                    Arguments.of(0L, "Comment Id가 0인 경우"),
                    Arguments.of(DBTypeSize.INT + 1, "Comment Id가 INT 최대값보다 큰 경우")
            );
        }

        @DisplayName("올바르지 않은 Post Id나 Comment Id가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidCommentId")
        @ParameterizedTest(name = "{index} : {1}")
        void givenInvalidPostIdOrCommentId_whenGetReply_thenReturnsBadRequest(Long invalidCommentId, String message) throws Exception {
            // given

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}/reply", invalidCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("Reply 조회가 비활성화되어 있을 때 일반 유저가 조회하면, 403 Forbidden을 반환한다.")
        @Test
        void givenReplyViewDisabled_whenGetReply_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(false).when(commentConfig).isViewEnable();

            var validCommentId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}/reply", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("Reply 조회가 비활성화되어 있을 때 관리자가 조회하면, 해당 Comment에 대한 Reply 목록을 반환한다.")
        @Test
        void givenReplyViewDisabled_whenGetReply_thenReturnsReplyList() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(false).when(commentConfig).isViewEnable();

            var validCommentId = 1L;

            var validReply = createReplyResponseCommonDto();
            var pagedReply = createPageObject(List.of(validReply), 0, 20, "createdDate", Sort.Direction.DESC);

            doReturn(pagedReply).when(commentService).getReply(validCommentId, pagedReply.getPageable());

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}/reply", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validReply, result, true);
        }

        private static Stream<Arguments> provideValidSortKey() {

            return Stream.of(
                    Arguments.of("id"),
                    Arguments.of("user"),
                    Arguments.of("createdDate"),
                    Arguments.of("lastModifiedDate")
            );
        }

        @DisplayName("올바른 Sort Key가 주어지면, 해당 Key로 Reply 목록을 정렬한다.")
        @MethodSource("provideValidSortKey")
        @ParameterizedTest(name = "{index} : {0} 가 주어졌을 때")
        void givenValidSortKey_whenGetReply_thenReturnsSortedReplyList(String validSortKey) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(true).when(commentConfig).isViewEnable();

            var validCommentId = 1L;

            var validReply = createReplyResponseCommonDto();
            var pagedReply = createPageObject(List.of(validReply), 0, 20, validSortKey, Sort.Direction.ASC);

            doReturn(pagedReply).when(commentService).getReply(validCommentId, pagedReply.getPageable());

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}/reply?sort=" + validSortKey, validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validReply, result, true);
        }

        @DisplayName("올바르지 않은 Sort Key가 주어지면, 400 Bad Request를 반환한다.")
        @Test
        void givenInvalidSortKey_whenGetReply_thenReturnsBadRequest() throws Exception {
            // given
            var notValidSortKey = "notValidSortKey";

            var validCommentId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}/reply?sort=" + notValidSortKey, validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("올바르지 않은 Page Size가 주어지면, 400 Bad Request를 반환한다.")
        @Test
        void givenInvalidPageSize_whenGetReply_thenReturnsBadRequest() throws Exception {
            // given
            var invalidPageSize = 51;


            var validCommentId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}/reply?size=" + invalidPageSize, validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAuthLevels() {
            return Stream.of(
                    Arguments.of(AuthLevel.ADMIN, "관리자 권한"),
                    Arguments.of(AuthLevel.USER, "일반 유저 권한"),
                    Arguments.of(AuthLevel.NONE, "인증 없음")
            );
        }

        @DisplayName("답글 조회가 활성화 되어 있을 때 권한 상관없이 답글 목록을 반환한다.")
        @MethodSource("provideAuthLevels")
        @ParameterizedTest(name = "{index} : {1} 일 때")
        void givenReplyViewEnabled_whenGetReply_thenReturnsReplyList(AuthLevel authLevel, String message) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);
            doReturn(true).when(commentConfig).isViewEnable();

            var validCommentId = 1L;

            var validReply = createReplyResponseCommonDto();
            var pagedReply = createPageObject(List.of(validReply), 0, 20, "createdDate", Sort.Direction.DESC);

            doReturn(pagedReply).when(commentService).getReply(validCommentId, pagedReply.getPageable());

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}/reply", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validReply, result, true);
        }

    }

    @DisplayName("createComment")
    @Nested
    class createComment {

        @DisplayName("올바른 Post Id와 Comment Request가 주어지면, Comment를 생성한다.")
        @Test
        void givenValidPostIdAndCommentRequest_whenCreateComment_thenCreatesComment() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();
            doReturn("1.1.1.1").when(requestContext).getIp();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validPostId = 1L;
            var validCommentRequest = CommentCreateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            var serviceDto = CommentCreateRequestServiceDto.builder()
                    .content(validCommentRequest.getContent())
                    .ip("1.1.1.1")
                    .postId(validPostId)
                    .build();

            var validComment = createCommentResponseCommonDto();

            doReturn(validComment).when(commentService).create(serviceDto);

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/v1/post/{postId}/comment", validPostId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(
                            document("comment/create",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(ResourceSnippetParameters.builder()
                                            .tag("Comment API")
                                            .summary("댓글을 생성합니다.(AUTH_LEVEL: USER)")
                                            .requestFields(
                                                    fieldWithPath("content").type(STRING).description("댓글 본문 2~1000자"),
                                                    fieldWithPath("captcha_token").type(STRING).description("캡차 토큰"),
                                                    fieldWithPath("im_not_bot").type(BOOLEAN).optional().description("honeypot")
                                            )
                                            .responseFields(
                                                    fieldWithPath("id").type(NUMBER).description("댓글 ID"),
                                                    fieldWithPath("post_id").type(NUMBER).description("포스트 ID"),
                                                    fieldWithPath("content").type(STRING).description("댓글 본문"),
                                                    fieldWithPath("writer.id").type(STRING).description("작성자 ID"),
                                                    fieldWithPath("writer.name").type(STRING).description("작성자 이름"),
                                                    fieldWithPath("writer.email").type(STRING).description("작성자 이메일"),
                                                    fieldWithPath("writer.profile_img").type(STRING).description("작성자 프로필 이미지"),
                                                    fieldWithPath("writer.admin").type(BOOLEAN).description("작성자 관리자 여부"),
                                                    fieldWithPath("writer.enabled").type(BOOLEAN).description("작성자 활성화 여부"),
                                                    fieldWithPath("created_date").type(STRING).description("작성일"),
                                                    fieldWithPath("last_modified_date").type(STRING).description("수정일"),
                                                    fieldWithPath("enabled").type(BOOLEAN).description("활성화 여부"),
                                                    subsectionWithPath("_links").ignored()
                                            )
                                            .requestSchema(Schema.schema("Comment.Create"))
                                            .responseSchema(Schema.schema("Comment.Response"))
                                            .build())
                            ));

            createMatcher(validComment, result, false);
        }

        private static Stream<Arguments> provideInvalidPostId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT에서 나올 수 없는 값"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 타입의 최대값보다 큰 값")
            );
        }

        @DisplayName("올바르지 않은 Post Id가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidPostId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidPostId_whenCreateComment_thenReturnsBadRequest(Long invalidPostId, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(captchaManager).verify("captchaToken");
            var validCommentRequest = CommentCreateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/post/{postId}/comment", invalidPostId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideInvalidCommentRequest() {
            var tooLongContent = CommentCreateRequestControllerDto.builder()
                    .content("a".repeat(1001))
                    .captchaToken("captchaToken")
                    .build();

            var tooShortContent = CommentCreateRequestControllerDto.builder()
                    .content("a")
                    .captchaToken("captchaToken")
                    .build();

            return Stream.of(
                    Arguments.of(tooLongContent, "본문이 1000자를 초과하는 경우"),
                    Arguments.of(tooShortContent, "본문이 2자 미만인 경우")
            );
        }

        @DisplayName("올바르지 않은 Comment Request가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidCommentRequest")
        @ParameterizedTest(name = "{index} : {1} 일 때")
        void givenInvalidCommentRequest_whenCreateComment_thenReturnsBadRequest(CommentCreateRequestControllerDto invalidCommentRequest, String message) throws Exception {
            // given
            var validPostId = 1L;
            doReturn(true).when(captchaManager).verify("captchaToken");

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/post/{postId}/comment", validPostId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(invalidCommentRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("Comment 작성이 비활성화되어 있을 때 일반 유저가 작성하면, 403 Forbidden을 반환한다.")
        @Test
        void givenCommentWriteDisabled_whenCreateComment_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(false).when(commentConfig).isWriteEnable();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validPostId = 1L;
            var validCommentRequest = CommentCreateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/post/{postId}/comment", validPostId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("Comment 작성이 비활성화되어 있을 때 관리자가 작성하면, Comment를 생성한다.")
        @Test
        void givenCommentWriteDisabled_whenCreateComment_thenCreatesComment() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(false).when(commentConfig).isWriteEnable();
            doReturn("1.1.1.1").when(requestContext).getIp();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validPostId = 1L;

            var validCommentRequest = CommentCreateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            var serviceDto = CommentCreateRequestServiceDto.builder()
                    .content(validCommentRequest.getContent())
                    .ip("1.1.1.1")
                    .postId(validPostId)
                    .build();

            var validComment = createCommentResponseCommonDto();

            doReturn(validComment).when(commentService).create(serviceDto);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/post/{postId}/comment", validPostId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validComment, result, false);
        }

        @DisplayName("로그인하지 않은 사용자가 댓글을 작성하면, 403 Forbidden을 반환한다.")
        @Test
        void givenNotLoggedInUser_whenCreateComment_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            var validPostId = 1L;
            var validCommentRequest = CommentCreateRequestControllerDto.builder()
                    .captchaToken("captchaToken")
                    .content("content")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/post/{postId}/comment", validPostId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("봇이 허니팟 필드를 채웠을 때, 400 Bad Request를 반환한다.")
        @Test
        void givenBot_whenCreateComment_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();
            doReturn(true).when(captchaManager).verify("captchaToken");
            var validPostId = 1L;

            var validCommentRequest = CommentCreateRequestControllerDto.builder()
                    .content("content")
                    .imNotBot(true)
                    .captchaToken("captchaToken")
                    .build();


            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/post/{postId}/comment", validPostId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("봇이 CAPTCHA를 통과하지 못했을 때, 400 Bad Request를 반환한다.")
        @Test
        void givenBotFailCaptcha_whenCreateComment_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();
            doReturn(false).when(captchaManager).verify("captchaToken");
            var validPostId = 1L;

            var validCommentRequest = CommentCreateRequestControllerDto.builder()
                    .content("content")
                    .imNotBot(true)
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/post/{postId}/comment", validPostId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());

        }

    }

    @DisplayName("createReply")
    @Nested
    class createReply {

        @DisplayName("올바른 Post Id, Comment Id와 Reply Request가 주어지면, Reply를 생성한다.")
        @Test
        void givenValidPostIdAndCommentIdAndReplyRequest_whenCreateReply_thenCreatesReply() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();
            doReturn("1.1.1.1").when(requestContext).getIp();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validPostId = 1L;

            var validCommentId = 1L;

            var validReplyRequest = ReplyCreateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            var serviceDto = ReplyCreateRequestServiceDto.builder()
                    .content(validReplyRequest.getContent())
                    .ip("1.1.1.1")
                    .postId(validPostId)
                    .parentId(validCommentId)
                    .build();

            var validReply = createCommentResponseCommonDto();

            doReturn(validReply).when(commentService).createReply(serviceDto);

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/v1/{postId}/comment/{commentId}/reply", validPostId, validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validReplyRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(
                            document("Comment/replyCreate",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(ResourceSnippetParameters.builder()
                                            .tag("Comment API")
                                            .summary("답글을 생성합니다.(AUTH_LEVEL: USER)")
                                            .pathParameters(
                                                    parameterWithName("postId").description("포스트 ID"),
                                                    parameterWithName("commentId").description("댓글 ID")
                                            )
                                            .requestFields(
                                                    fieldWithPath("content").type(STRING).description("답글 본문 2~1000자"),
                                                    fieldWithPath("captcha_token").type(STRING).description("캡차 토큰"),
                                                    fieldWithPath("im_not_bot").type(BOOLEAN).optional().description("honeypot")
                                            )
                                            .responseFields(
                                                    fieldWithPath("id").type(NUMBER).description("답글 ID"),
                                                    fieldWithPath("post_id").type(NUMBER).description("포스트 ID"),
                                                    fieldWithPath("content").type(STRING).description("답글 본문"),
                                                    fieldWithPath("writer.id").type(STRING).description("작성자 ID"),
                                                    fieldWithPath("writer.name").type(STRING).description("작성자 이름"),
                                                    fieldWithPath("writer.email").type(STRING).description("작성자 이메일"),
                                                    fieldWithPath("writer.profile_img").type(STRING).description("작성자 프로필 이미지"),
                                                    fieldWithPath("writer.admin").type(BOOLEAN).description("작성자 관리자 여부"),
                                                    fieldWithPath("writer.enabled").type(BOOLEAN).description("작성자 활성화 여부"),
                                                    fieldWithPath("created_date").type(STRING).description("작성일"),
                                                    fieldWithPath("last_modified_date").type(STRING).description("수정일"),
                                                    fieldWithPath("enabled").type(BOOLEAN).description("활성화 여부"),
                                                    subsectionWithPath("_links").ignored()
                                            )
                                            .requestSchema(Schema.schema("Comment.ReplyCreate"))
                                            .responseSchema(Schema.schema("Comment.Response"))
                                            .build())
                            ));

            createMatcher(validReply, result, false);
        }

        private static Stream<Arguments> provideInvalidPostIdAndCommentId() {
            return Stream.of(
                    Arguments.of(0L, 1L, "Post Id가 0인 경우"),
                    Arguments.of(DBTypeSize.INT + 1, 1L, "Post Id가 INT 최대값보다 큰 경우"),
                    Arguments.of(1L, 0L, "Comment Id가 0인 경우"),
                    Arguments.of(1L, DBTypeSize.INT + 1, "Comment Id가 INT 최대값보다 큰 경우")
            );
        }

        @DisplayName("올바르지 않은 Post Id나 Comment Id가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidPostIdAndCommentId")
        @ParameterizedTest(name = "{index} : {2}")
        void givenInvalidPostIdOrCommentId_whenCreateReply_thenReturnsBadRequest(Long invalidPostId, Long invalidCommentId, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validReplyRequest = ReplyCreateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/{postId}/comment/{commentId}/reply", invalidPostId, invalidCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validReplyRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideInvalidReplyRequest() {
            var tooLongContent = ReplyCreateRequestControllerDto.builder()
                    .content("a".repeat(1001))
                    .captchaToken("captchaToken")
                    .build();

            var tooShortContent = ReplyCreateRequestControllerDto.builder()
                    .content("a")
                    .captchaToken("captchaToken")
                    .build();

            return Stream.of(
                    Arguments.of(tooLongContent, "본문이 1000자를 초과하는 경우"),
                    Arguments.of(tooShortContent, "본문이 2자 미만인 경우")
            );
        }

        @DisplayName("올바르지 않은 Reply Request가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidReplyRequest")
        @ParameterizedTest(name = "{index} : {1} 일 때")
        void givenInvalidReplyRequest_whenCreateReply_thenReturnsBadRequest(ReplyCreateRequestControllerDto invalidReplyRequest, String message) throws Exception {
            // given
            var validPostId = 1L;
            var validCommentId = 1L;

            doReturn(true).when(captchaManager).verify("captchaToken");
            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/{postId}/comment/{commentId}/reply", validPostId, validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(invalidReplyRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("Reply 작성이 비활성화되어 있을 때 일반 유저가 작성하면, 403 Forbidden을 반환한다.")
        @Test
        void givenReplyWriteDisabled_whenCreateReply_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(false).when(commentConfig).isWriteEnable();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validPostId = 1L;
            var validCommentId = 1L;

            var validReplyRequest = ReplyCreateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/{postId}/comment/{commentId}/reply", validPostId, validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validReplyRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("Reply 작성이 비활성화되어 있을 때 관리자가 작성하면, Reply를 생성한다.")
        @Test
        void givenReplyWriteDisabled_whenCreateReply_thenCreatesReply() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(false).when(commentConfig).isWriteEnable();
            doReturn("1.1.1.1").when(requestContext).getIp();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validPostId = 1L;

            var validCommentId = 1L;

            var validReplyRequest = ReplyCreateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            var serviceDto = ReplyCreateRequestServiceDto.builder()
                    .content(validReplyRequest.getContent())
                    .ip("1.1.1.1")
                    .postId(validPostId)
                    .parentId(validCommentId)
                    .build();

            var validReply = createCommentResponseCommonDto();

            doReturn(validReply).when(commentService).createReply(serviceDto);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/{postId}/comment/{commentId}/reply", validPostId, validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validReplyRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validReply, result, false);
        }

        @DisplayName("로그인하지 않은 사용자가 답글을 작성하면, 403 Forbidden을 반환한다.")
        @Test
        void givenNotLoggedInUser_whenCreateReply_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validPostId = 1L;
            var validCommentId = 1L;

            var validReplyRequest = ReplyCreateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/{postId}/comment/{commentId}/reply", validPostId, validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validReplyRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("봇이 허니팟 필드를 채웠을 때, 400 Bad Request를 반환한다.")
        @Test
        void givenBot_whenCreateReply_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validPostId = 1L;
            var validCommentId = 1L;

            var validReplyRequest = ReplyCreateRequestControllerDto.builder()
                    .content("content")
                    .imNotBot(true)
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/{postId}/comment/{commentId}/reply", validPostId, validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validReplyRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("봇이 CAPTCHA를 통과하지 못했을 때, 400 Bad Request를 반환한다.")
        @Test
        void givenBotFailCaptcha_whenCreateReply_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();
            doReturn(false).when(captchaManager).verify("captchaToken");

            var validPostId = 1L;
            var validCommentId = 1L;

            var validReplyRequest = ReplyCreateRequestControllerDto.builder()
                    .content("content")
                    .imNotBot(true)
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/{postId}/comment/{commentId}/reply", validPostId, validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validReplyRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @DisplayName("updateComment")
    @Nested
    class updateComment {

        @DisplayName("올바른 Comment Id와 Comment Update Request가 주어지면, Comment를 수정한다.")
        @Test
        void givenValidCommentIdAndCommentUpdateRequest_whenUpdateComment_thenUpdatesComment() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validCommentId = 1L;

            var validCommentUpdateRequest = CommentUpdateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            var serviceDto = CommentUpdateRequestServiceDto.builder()
                    .content(validCommentUpdateRequest.getContent())
                    .id(validCommentId)
                    .build();

            var validComment = createCommentResponseCommonDto();

            doReturn(validComment).when(commentService).update(serviceDto);

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.put("/api/v1/comment/{commentId}", validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentUpdateRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(
                            document("Comment/update",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(ResourceSnippetParameters.builder()
                                            .tag("Comment API")
                                            .summary("댓글을 수정합니다.(AUTH_LEVEL: USER)")
                                            .description("자기 자신의 댓글만 수정 가능합니다.")
                                            .pathParameters(
                                                    parameterWithName("commentId").description("댓글 ID")
                                            )
                                            .requestFields(
                                                    fieldWithPath("content").type(STRING).description("댓글 본문 2~1000자"),
                                                    fieldWithPath("captcha_token").type(STRING).description("캡차 토큰"),
                                                    fieldWithPath("im_not_bot").type(BOOLEAN).optional().description("honeypot")
                                            )
                                            .responseFields(
                                                    fieldWithPath("id").type(NUMBER).description("댓글 ID"),
                                                    fieldWithPath("post_id").type(NUMBER).description("포스트 ID"),
                                                    fieldWithPath("content").type(STRING).description("댓글 본문"),
                                                    fieldWithPath("writer.id").type(STRING).description("작성자 ID"),
                                                    fieldWithPath("writer.name").type(STRING).description("작성자 이름"),
                                                    fieldWithPath("writer.email").type(STRING).description("작성자 이메일"),
                                                    fieldWithPath("writer.profile_img").type(STRING).description("작성자 프로필 이미지"),
                                                    fieldWithPath("writer.admin").type(BOOLEAN).description("작성자 관리자 여부"),
                                                    fieldWithPath("writer.enabled").type(BOOLEAN).description("작성자 활성화 여부"),
                                                    fieldWithPath("created_date").type(STRING).description("작성일"),
                                                    fieldWithPath("last_modified_date").type(STRING).description("수정일"),
                                                    fieldWithPath("enabled").type(BOOLEAN).description("활성화 여부"),
                                                    subsectionWithPath("_links").ignored()
                                            )
                                            .requestSchema(Schema.schema("Comment.Update"))
                                            .responseSchema(Schema.schema("Comment.Response"))
                                            .build())
                            ));

            createMatcher(validComment, result, false);
        }

        private static Stream<Arguments> provideInvalidCommentId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT에서 나올 수 없는 값"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 타입의 최대값보다 큰 값")
            );
        }

        @DisplayName("올바르지 않은 Comment Id가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidCommentId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidCommentId_whenUpdateComment_thenReturnsBadRequest(Long invalidCommentId, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validCommentUpdateRequest = CommentUpdateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}", invalidCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentUpdateRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideInvalidCommentUpdateRequest() {
            var tooLongContent = CommentUpdateRequestControllerDto.builder()
                    .content("a".repeat(1001))
                    .captchaToken("captchaToken")
                    .build();

            var tooShortContent = CommentUpdateRequestControllerDto.builder()
                    .content("a")
                    .captchaToken("captchaToken")
                    .build();

            return Stream.of(
                    Arguments.of(tooLongContent, "본문이 1000자를 초과하는 경우"),
                    Arguments.of(tooShortContent, "본문이 2자 미만인 경우")
            );
        }

        @DisplayName("올바르지 않은 Comment Update Request가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidCommentUpdateRequest")
        @ParameterizedTest(name = "{index} : {1} 일 때")
        void givenInvalidCommentUpdateRequest_whenUpdateComment_thenReturnsBadRequest(CommentUpdateRequestControllerDto invalidCommentUpdateRequest, String message) throws Exception {
            // given
            doReturn(true).when(captchaManager).verify("captchaToken");
            var validCommentId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}", validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(invalidCommentUpdateRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("Comment 수정이 비활성화되어 있을 때 일반 유저가 수정하면, 403 Forbidden을 반환한다.")
        @Test
        void givenCommentUpdateDisabled_whenUpdateComment_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(false).when(commentConfig).isWriteEnable();
            doReturn(true).when(captchaManager).verify("captchaToken");
            var validCommentId = 1L;

            var validCommentUpdateRequest = CommentUpdateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}", validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentUpdateRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("Comment 수정이 비활성화되어 있을 때 관리자가 수정하면, Comment를 수정한다.")
        @Test
        void givenCommentUpdateDisabled_whenUpdateComment_thenUpdatesComment() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(false).when(commentConfig).isWriteEnable();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validCommentId = 1L;

            var validCommentUpdateRequest = CommentUpdateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            var serviceDto = CommentUpdateRequestServiceDto.builder()
                    .content(validCommentUpdateRequest.getContent())
                    .id(validCommentId)
                    .build();

            var validComment = createCommentResponseCommonDto();

            doReturn(validComment).when(commentService).update(serviceDto);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}", validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentUpdateRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validComment, result, false);
        }

        @DisplayName("로그인하지 않은 사용자가 댓글을 수정하면, 403 Forbidden을 반환한다.")
        @Test
        void givenNotLoggedInUser_whenUpdateComment_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.NONE);
            doReturn(true).when(captchaManager).verify("captchaToken");
            var validCommentId = 1L;

            var validCommentUpdateRequest = CommentUpdateRequestControllerDto.builder()
                    .content("content")
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}", validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentUpdateRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("봇이 HoneyPot 필드를 채웠을 때, 400 Bad Request를 반환한다.")
        @Test
        void givenHoneyPotField_whenUpdateComment_thenReturnsBadRequest() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();
            doReturn(true).when(captchaManager).verify("captchaToken");

            var validCommentId = 1L;

            var validCommentUpdateRequest = CommentUpdateRequestControllerDto.builder()
                    .content("content")
                    .imNotBot(true)
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}", validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentUpdateRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("봇이 CAPTCHA를 통과하지 못했을 때, 400 Bad Request를 반환한다.")
        @Test
        void givenBotFailCaptcha_whenUpdateComment_thenReturnsBadRequest() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();
            doReturn(false).when(captchaManager).verify("captchaToken");

            var validCommentId = 1L;

            var validCommentUpdateRequest = CommentUpdateRequestControllerDto.builder()
                    .content("content")
                    .imNotBot(false)
                    .captchaToken("captchaToken")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}", validCommentId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(validCommentUpdateRequest))
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }
    }

    @DisplayName("updateCommentStatus")
    @Nested
    class updateCommentStatus {

        @DisplayName("올바른 Comment Id가 주어지면, Comment의 상태를 수정한다.")
        @Test
        void givenValidCommentId_whenUpdateCommentStatus_thenUpdatesCommentStatus() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(true).when(commentConfig).isWriteEnable();

            var validCommentId = 1L;

            var validComment = createCommentResponseCommonDto();

            doReturn(validComment).when(commentService).disabled(validCommentId);

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.put("/api/v1/comment/{commentId}/status", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(
                            document("Comment/updateStatus",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(ResourceSnippetParameters.builder()
                                            .tag("Comment API")
                                            .summary("댓글의 상태를 수정합니다.(AUTH_LEVEL: USER)")
                                            .description("유저는 댓글을 영구적으로 삭제할 수 없습니다. 따라서 해당 API로 댓글을 비활성화할 수 있습니다.")
                                            .pathParameters(
                                                    parameterWithName("commentId").description("댓글 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("id").type(NUMBER).description("댓글 ID"),
                                                    fieldWithPath("post_id").type(NUMBER).description("포스트 ID"),
                                                    fieldWithPath("content").type(STRING).description("댓글 본문"),
                                                    fieldWithPath("writer.id").type(STRING).description("작성자 ID"),
                                                    fieldWithPath("writer.name").type(STRING).description("작성자 이름"),
                                                    fieldWithPath("writer.email").type(STRING).description("작성자 이메일"),
                                                    fieldWithPath("writer.profile_img").type(STRING).description("작성자 프로필 이미지"),
                                                    fieldWithPath("writer.admin").type(BOOLEAN).description("작성자 관리자 여부"),
                                                    fieldWithPath("writer.enabled").type(BOOLEAN).description("작성자 활성화 여부"),
                                                    fieldWithPath("created_date").type(STRING).description("작성일"),
                                                    fieldWithPath("last_modified_date").type(STRING).description("수정일"),
                                                    fieldWithPath("enabled").type(BOOLEAN).description("활성화 여부"),
                                                    subsectionWithPath("_links").ignored()
                                            )
                                            .responseSchema(Schema.schema("Comment.Response"))
                                            .build())
                            ));

            createMatcher(validComment, result, false);
        }

        private static Stream<Arguments> provideInvalidCommentId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT에서 나올 수 없는 값"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 타입의 최대값보다 큰 값")
            );
        }

        @DisplayName("올바르지 않은 Comment Id가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidCommentId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidCommentId_whenUpdateCommentStatus_thenReturnsBadRequest(Long invalidCommentId, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}/status", invalidCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("Comment 수정이 비활성화되어 있을 때 일반 유저가 수정하면, 403 Forbidden을 반환한다.")
        @Test
        void givenCommentUpdateDisabled_whenUpdateCommentStatus_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(false).when(commentConfig).isWriteEnable();

            var validCommentId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}/status", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("Comment 수정이 비활성화되어 있을 때 관리자가 수정하면, Comment의 상태를 수정한다.")
        @Test
        void givenCommentUpdateDisabled_whenUpdateCommentStatus_thenUpdatesCommentStatus() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(false).when(commentConfig).isWriteEnable();

            var validCommentId = 1L;

            var validComment = createCommentResponseCommonDto();

            doReturn(validComment).when(commentService).disabled(validCommentId);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}/status", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validComment, result, false);
        }

        @DisplayName("로그인하지 않은 사용자가 댓글을 수정하면, 403 Forbidden을 반환한다.")
        @Test
        void givenNotLoggedInUser_whenUpdateCommentStatus_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.NONE);

            var validCommentId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/comment/{commentId}/status", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @DisplayName("getCommentById")
    @Nested
    class getCommentById {

        @DisplayName("올바른 Comment Id가 주어지면, 해당 Comment를 반환한다.")
        @Test
        void givenValidCommentId_whenGetCommentById_thenReturns() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(true).when(commentConfig).isViewEnable();

            var validCommentId = 1L;

            var validComment = createCommentResponseCommonDto();

            doReturn(validComment).when(commentService).getCommentById(validCommentId);

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v1/comment/{commentId}", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(
                            document("Comment/getById",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(ResourceSnippetParameters.builder()
                                            .tag("Comment API")
                                            .summary("댓글을 조회합니다. (AUTH_LEVEL: NONE)")
                                            .description("댓글의 상세 정보를 조회합니다. 만약 댓글이 비활성화 되어있으면, 본문을 확인할 수 없습니다. 관리자의 경우 비활성화 여부와 상관없이 조회 가능합니다.")
                                            .pathParameters(
                                                    parameterWithName("commentId").description("댓글 ID")
                                            )
                                            .responseFields(
                                                    fieldWithPath("id").type(NUMBER).description("댓글 ID"),
                                                    fieldWithPath("post_id").type(NUMBER).description("포스트 ID"),
                                                    fieldWithPath("content").type(STRING).description("댓글 본문"),
                                                    fieldWithPath("writer.id").type(STRING).description("작성자 ID"),
                                                    fieldWithPath("writer.name").type(STRING).description("작성자 이름"),
                                                    fieldWithPath("writer.email").type(STRING).description("작성자 이메일"),
                                                    fieldWithPath("writer.profile_img").type(STRING).description("작성자 프로필 이미지"),
                                                    fieldWithPath("writer.admin").type(BOOLEAN).description("작성자 관리자 여부"),
                                                    fieldWithPath("writer.enabled").type(BOOLEAN).description("작성자 활성화 여부"),
                                                    fieldWithPath("created_date").type(STRING).description("작성일"),
                                                    fieldWithPath("last_modified_date").type(STRING).description("수정일"),
                                                    fieldWithPath("enabled").type(BOOLEAN).description("활성화 여부"),
                                                    subsectionWithPath("_links").ignored()
                                            )
                                            .responseSchema(Schema.schema("Comment.Response"))
                                            .build())
                            ));

            createMatcher(validComment, result, false);
        }

        private static Stream<Arguments> provideInvalidCommentId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT에서 나올 수 없는 값"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 타입의 최대값보다 큰 값")
            );
        }

        @DisplayName("올바르지 않은 Comment Id가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidCommentId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidCommentId_whenGetCommentById_thenReturns(Long id) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(true).when(commentConfig).isViewEnable();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}", id)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        @DisplayName("Comment 조회가 비활성화되어 있을 때 일반 유저가 조회하면, 403 Forbidden을 반환한다.")
        @Test
        void givenCommentViewDisabled_whenGetCommentById_thenReturnsForbidden() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);
            doReturn(false).when(commentConfig).isViewEnable();

            var validCommentId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

        @DisplayName("Comment 조회가 비활성화되어 있을 때 관리자가 조회하면, 해당 Comment를 반환한다.")
        @Test
        void givenCommentViewDisabled_whenGetCommentById_thenReturns() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            doReturn(false).when(commentConfig).isViewEnable();

            var validCommentId = 1L;

            var validComment = createCommentResponseCommonDto();

            doReturn(validComment).when(commentService).getCommentById(validCommentId);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validComment, result, false);
        }

        private static Stream<Arguments> provideAuthLevels() {
            return Stream.of(
                    Arguments.of(AuthLevel.ADMIN, "관리자 권한"),
                    Arguments.of(AuthLevel.USER, "일반 유저 권한"),
                    Arguments.of(AuthLevel.NONE, "인증 없음")
            );
        }

        @DisplayName("댓글 조회가 활성화 되어 있을 때 권한 상관없이 댓글을 반환한다.")
        @MethodSource("provideAuthLevels")
        @ParameterizedTest(name = "{index} : {1} 일 때")
        void givenCommentViewEnabled_whenGetCommentById_thenReturns(AuthLevel authLevel, String message) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);
            doReturn(true).when(commentConfig).isViewEnable();

            var validCommentId = 1L;

            var validComment = createCommentResponseCommonDto();

            doReturn(validComment).when(commentService).getCommentById(validCommentId);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/comment/{commentId}", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validComment, result, false);
        }
    }

    @DisplayName("deleteComment")
    @Nested
    class deleteComment {

        @DisplayName("올바른 Comment Id가 주어지면, 해당 Comment를 삭제한다.")
        @Test
        void givenValidCommentId_whenDeleteComment_thenDeletesComment() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validCommentId = 1L;

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.delete("/api/v1/comment/{commentId}", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isNoContent())
                    .andDo(
                            document("Comment/delete",
                                    preprocessRequest(prettyPrint()),
                                    preprocessResponse(prettyPrint()),
                                    resource(ResourceSnippetParameters.builder()
                                            .tag("Comment API")
                                            .summary("댓글을 삭제합니다.(AUTH_LEVEL: ADMIN)")
                                            .description("댓글을 삭제합니다.")
                                            .pathParameters(
                                                    parameterWithName("commentId").description("댓글 ID")
                                            )
                                            .build())
                            ));
        }

        private static Stream<Arguments> provideInvalidCommentId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT에서 나올 수 없는 값"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 타입의 최대값보다 큰 값")
            );
        }

        @DisplayName("올바르지 않은 Comment Id가 주어지면, 400 Bad Request를 반환한다.")
        @MethodSource("provideInvalidCommentId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidCommentId_whenDeleteComment_thenReturnsBadRequest(Long id) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/comment/{commentId}", id)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAuthLevels() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "일반 유저 권한"),
                    Arguments.of(AuthLevel.NONE, "인증 없음")
            );
        }

        @DisplayName("관리자가 아닌 사용자가 댓글을 삭제하면, 403 Forbidden을 반환한다.")
        @MethodSource("provideAuthLevels")
        @ParameterizedTest(name = "{index} : {1} 일 때")
        void givenNotAdmin_whenDeleteComment_thenReturnsForbidden(AuthLevel authLevel, String message) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            var validCommentId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/comment/{commentId}", validCommentId)
            );

            // then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }


    }


}
