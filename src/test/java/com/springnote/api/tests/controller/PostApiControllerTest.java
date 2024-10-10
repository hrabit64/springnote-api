package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.epages.restdocs.apispec.SimpleType;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.domain.post.PostKeywordSearchMode;
import com.springnote.api.domain.post.PostQueryKeys;
import com.springnote.api.dto.assembler.post.PostDetailResponseCommonDtoAssembler;
import com.springnote.api.dto.assembler.post.PostSimpleResponseCommonDtoAssembler;
import com.springnote.api.dto.general.common.PostTagId;
import com.springnote.api.dto.post.controller.PostCreateRequestControllerDto;
import com.springnote.api.dto.post.controller.PostStatusUpdateRequestControllerDto;
import com.springnote.api.dto.post.controller.PostUpdateRequestControllerDto;
import com.springnote.api.dto.post.service.PostCreateRequestServiceDto;
import com.springnote.api.dto.post.service.PostStatusUpdateRequestServiceDto;
import com.springnote.api.dto.post.service.PostUpdateRequestServiceDto;
import com.springnote.api.service.PostService;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.type.TypeParser;
import com.springnote.api.web.controller.PostApiController;
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
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Stream;

import static com.epages.restdocs.apispec.MockMvcRestDocumentationWrapper.document;
import static com.epages.restdocs.apispec.ResourceDocumentation.parameterWithName;
import static com.epages.restdocs.apispec.ResourceDocumentation.resource;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createPageObject;
import static com.springnote.api.testUtils.dataFactory.TestDataFactory.createUserContextReturns;
import static com.springnote.api.testUtils.dataFactory.post.PostDtoTestDataFactory.*;
import static com.springnote.api.testUtils.docs.PostResponseFieldGenerator.postDetail;
import static com.springnote.api.testUtils.docs.PostResponseFieldGenerator.postPagedSimple;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller Test - PostApiController")
@WebMvcTest(PostApiController.class)
public class PostApiControllerTest extends ControllerTestTemplate {

    @Autowired
    private PostApiController postApiController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PostService postService;

    @SpyBean
    private PostDetailResponseCommonDtoAssembler postDetailAssembler;

    @SpyBean
    private PostSimpleResponseCommonDtoAssembler postSimpleAssembler;

    @SpyBean
    private TypeParser typeParser;


    @DisplayName("getPostById")
    @Nested
    class getPostById {

        @DisplayName("올바른 포스트 ID가 주어지면 해당 포스트를 반환한다.")
        @Test
        void givenValidPostId_whenGetPostById_thenReturns() throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validPostId = 1L;

            var validPostItem = createPostDetailResponseCommonDto();

            doReturn(validPostItem).when(postService).getById(validPostId);

            // When
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/{id}", validPostId));

            // Then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("post/getPostById",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Post API")
                                    .summary("포스트 ID로 포스트를 조회합니다. (AUTH-LEVEL : NONE)")
                                    .description("해당 하는 포스트 ID를 가진 포스트를 반환합니다. 이때 비활성화된 포스트는 관리자만 조회할 수 있습니다.")
                                    .pathParameters(
                                            parameterWithName("id").type(SimpleType.NUMBER).description("조회할 대상 포스트 ID, (1~4294967295)")
                                    )
                                    .responseFields(
                                            postDetail()
                                    )
                                    .responseSchema(Schema.schema("Post.Detail"))
                                    .build())));

            createMatcher(validPostItem, result, false);


        }

        private static Stream<Arguments> provideInvalidPostId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT 에서 나올 수 없는 수"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 최대값 보다 큰 수")
            );
        }

        @DisplayName("유효하지 않은 포스트 ID가 주어지면 400 에러를 반환한다.")
        @MethodSource("provideInvalidPostId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidPostId_whenGetPostById_thenReturns(Long invalidPostId, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post/{id}", invalidPostId));

            // Then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.ADMIN, "ADMIN"),
                    Arguments.of(AuthLevel.NONE, "NONE"),
                    Arguments.of(AuthLevel.USER, "USER")
            );
        }

        @DisplayName("활성화 포스트는 권한 상관없이 조회할 수 있다.")
        @MethodSource("provideAuthLevel")
        @ParameterizedTest(name = "{index} : {1} 권한으로 조회할 때")
        void givenEnabledPost_whenGetPostById_thenReturns(AuthLevel authLevel, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, authLevel);

            var validPostId = 1L;

            var validPostItem = createPostDetailResponseCommonDto();

            doReturn(validPostItem).when(postService).getById(validPostId);

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post/{id}", validPostId));

            // Then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validPostItem, result, false);
        }

        @DisplayName("관리자가 비활성화 포스트를 조회하면, 해당 포스트를 리턴한다.")
        @Test
        void givenDisabledPost_whenGetPostById_thenReturns() throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validPostId = 1L;

            var validPostItem = createPostDetailResponseCommonDto();
            validPostItem.setEnabled(false);

            doReturn(validPostItem).when(postService).getById(validPostId);

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post/{id}", validPostId));

            // Then
            result.andDo(print())
                    .andExpect(status().isOk());

            createMatcher(validPostItem, result, false);
        }

        private static Stream<Arguments> provideNotAllowAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.NONE, "NONE"),
                    Arguments.of(AuthLevel.USER, "USER")
            );
        }

        @DisplayName("비활성화 포스트를 조회할 때, 권한이 없으면 403 에러를 반환한다.")
        @MethodSource("provideNotAllowAuthLevel")
        @ParameterizedTest(name = "{index} : {1} 권한으로 조회할 때")
        void givenDisabledPost_whenGetPostByIdAndNoAdmin_thenReturns403(AuthLevel authLevel, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, authLevel);

            var validPostId = 1L;

            var validPostItem = createPostDetailResponseCommonDto();
            validPostItem.setEnabled(false);

            doReturn(validPostItem).when(postService).getById(validPostId);

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post/{id}", validPostId));

            // Then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

    }

    @DisplayName("getTitleIsExist")
    @Nested
    class getTitleIsExist {

        @DisplayName("올바른 제목이 주어지면 해당 제목이 존재하는지 확인한다.")
        @Test
            //중복인지 아닌지가 중요한 부분이 아니므로, 테스트 코드에서는 중복이 아닌 경우로 테스트
        void givenValidTitle_whenGetTitleIsExist_thenReturns() throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var uniqueTitle = "uniqueTitle";

            doReturn(false).when(postService).isExistTitle(uniqueTitle);

            // When
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post/title/{title}", uniqueTitle));

            // Then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.passed").value(false))
                    .andDo(document("post/getTitleIsExist",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Post API")
                                    .summary("해당 제목을 가진 포스트가 있는지 검색합니다. (AUTH-LEVEL : ADMIN)")
                                    .pathParameters(
                                            parameterWithName("title").type(SimpleType.STRING).description("조회할 대상 포스트의 이름, 3~300자")
                                    )
                                    .responseFields(
                                            fieldWithPath("passed").type(BOOLEAN).description("중복되는 제목이 존재하는지 여부")
                                    )
                                    .responseSchema(Schema.schema("Post.TitleSearchResult"))
                                    .build())));
        }

        private static Stream<Arguments> provideInvalidTitle() {
            return Stream.of(
                    Arguments.of("a", "제목이 3자 미만인 경우"),
                    Arguments.of("a".repeat(301), "제목이 300자를 초과하는 경우")
            );
        }

        @DisplayName("유효하지 않은 제목이 주어지면 400 에러를 반환한다.")
        @MethodSource("provideInvalidTitle")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidTitle_whenGetTitleIsExist_thenReturns(String invalidTitle, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post/title/{title}", invalidTitle));

            // Then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER"),
                    Arguments.of(AuthLevel.NONE, "NONE"));

        }

        @DisplayName("괸라자가 아닌 사용자가 요청하면, 403 에러를 반환한다.")
        @MethodSource("provideAuthLevel")
        @ParameterizedTest(name = "{index} : {1} 권한으로 조회할 때")
        void givenNotAdmin_whenGetTitleIsExist_thenReturns403(AuthLevel authLevel, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, authLevel);

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post/title/{title}", "title"));

            // Then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @DisplayName("getPosts")
    @Nested
    class getPosts {

        // 검증 로직 자체는 동일하고, 각각의 메소드에 영향을 주지 않으므로, NONE 모드의 getAllByQueryParams 메소드로 테스트 한다.
        @DisplayName("getPosts - 공통")
        @Nested
        class getPost_common {

            private static Stream<Arguments> provideVaildSearchMode() {
                return Stream.of(
                        Arguments.of(PostKeywordSearchMode.TITLE),
                        Arguments.of(PostKeywordSearchMode.CONTENT),
                        Arguments.of(PostKeywordSearchMode.MIX)
                );
            }

            @DisplayName("올바른 Keyword 와 올바른 SearchMode 가 주어지면, 해당 포스트를 반환한다.")
            @MethodSource("provideVaildSearchMode")
            @ParameterizedTest(name = "{index} : {0} 가 주어졌을 때")
            void givenKeywordAndSearchMode_whenGetPosts_thenReturns(PostKeywordSearchMode searchMode) throws Exception {
                // Given
                createUserContextReturns(userContext, AuthLevel.NONE);

                var queryParam = createQueryParam("keyWord", "some", "searchMode", searchMode.name());

                var processedQueryParam = createTypedQueryParam(PostQueryKeys.IS_ONLY_OPEN_POST, "true");
                var validPostItem = createPostSimpleResponseCommonDto();


                var pagedItem = createPageObject(List.of(validPostItem), 0, 20, "createdDate", Sort.Direction.DESC);

                switch (searchMode) {
                    case TITLE:
                        doReturn(pagedItem).when(postService).getAllByTitleKeyword("some", processedQueryParam, pagedItem.getPageable());
                        break;
                    case CONTENT:
                        doReturn(pagedItem).when(postService).getAllByContentKeyword("some", processedQueryParam, pagedItem.getPageable());
                        break;
                    case MIX:
                        doReturn(pagedItem).when(postService).getAllByMixKeyword("some", processedQueryParam, pagedItem.getPageable());
                        break;
                }

                // When
                var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/post")
                        .params(queryParam));

                // Then
                result.andDo(print())
                        .andExpect(status().isOk())
                        .andDo(document("post/getPosts",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                resource(ResourceSnippetParameters.builder()
                                        .tag("Post API")
                                        .summary("포스트를 조회합니다. (AUTH-LEVEL : NONE)")
                                        .description(
                                                "* 사용 가능 쿼리 파라미터 (대소문자 구분 없음)\n" +
                                                        "\n" +
                                                        "|       키       |                             설명                             |                 제약                 |                기본값                |\n" +
                                                        "| :------------: | :----------------------------------------------------------: | :----------------------------------: | :----------------------------------: |\n" +
                                                        "|    postType    |                     포스트의 유형입니다.                     |                                      |                  x                   |\n" +
                                                        "|     series     |                   포스트의 시리즈 입니다.                    |                                      |                  x                   |\n" +
                                                        "|      tag       |                  포스트의 태그 정보 입니다.                  | 최대 10개 까지만 사용가능, 중복 불가 |                  x                   |\n" +
                                                        "| isOnlyOpenPost | 공개된 포스트만 검색할지 여부입니다. true면 공개된 포스트만 검색합니다. *단  false 사용시에는 관리자 권한을 요구합니다.* |            1개만 사용가능            | 유저일경우 true, 관리자일 경우 false |\n" +
                                                        "\n" +
                                                        "* 페이징\n" +
                                                        "\n" +
                                                        "해당 API는 페이징을 지원합니다.\n" +
                                                        "\n" +
                                                        "|  키  |          설명           |            제약            |      기본값      |\n" +
                                                        "| :--: | :---------------------: | :------------------------: | :--------------: |\n" +
                                                        "| size |      페이징 사이즈      |        최대 100까지        |        20        |\n" +
                                                        "| page | 페이지 넘버 *0부터 시작 |                            |        0         |\n" +
                                                        "| sort |        정렬 옵션        | *아래 사용 가능 키만 가능* | createdDate;DESC |\n" +
                                                        "\n" +
                                                        "사용 가능한 sort키는 아래와 같습니다. *모든 Sort 사용시 방향을 지정하지 않으면 ASC 로 동작합니다. (대소문자 구분 없음)*\n" +
                                                        "\n" +
                                                        "|        키        |     설명      |\n" +
                                                        "| :--------------: | :-----------: |\n" +
                                                        "|        id        |   포스트 ID   |\n" +
                                                        "|      title       |  포스트 제목  |\n" +
                                                        "|     content      |  포스트 본문  |\n" +
                                                        "|      isOpen      |   공개 여부   |\n" +
                                                        "|      series      |    시리즈     |\n" +
                                                        "| lastModifiedDate | 마지막 수정일 |\n" +
                                                        "|   createdDate    |    생성일     |\n" +
                                                        "\n" +
                                                        "* 키워드 검색\n" +
                                                        "제목, 본문, 제목 + 본문에 대한 키워드 검색을 지원합니다. 쿼리파라미터에 아래 키를 사용하면 키워드 검색을 수행할 수 있습니다. 이때 아래 두개의 키가 모두 입력되지 않았을 경우 에러가 발생합니다.\n" +
                                                        "\n" +
                                                        "|     키     |                            설명                            |         제약          |\n" +
                                                        "| :--------: | :--------------------------------------------------------: | :-------------------: |\n" +
                                                        "|  keyword   |                       검색할 키워드                        |        2~20자         |\n" +
                                                        "| searchMode | 검색 모드, TITLE(제목),CONTENT(본문),MIX(제목+본문) 중 1택 | 1개의 옵션만 설정가능 |\n" +
                                                        "\n"
                                        )
                                        .responseFields(
                                                postPagedSimple()
                                        )
                                        .responseSchema(Schema.schema("Post.PagedSimple"))
                                        .build())));

                createMatcher(validPostItem, result, true);

            }

            private static Stream<Arguments> provideValidQueryParam() {

                // 모든 쿼리파라미터는 대소문자 구분이 없다.
                return Stream.of(
                        //postType
                        Arguments.of(createQueryParam("postType", "1"), createTypedQueryParam(PostQueryKeys.POST_TYPE, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 포스트 타입이 카멜케이스 (?postType=1)이(가) 주어졌을 때"),
                        Arguments.of(createQueryParam("posttype", "1"), createTypedQueryParam(PostQueryKeys.POST_TYPE, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 포스트 타입이 소문자로만 (?posttype=1)이(가) 주어졌을 때 "),
                        Arguments.of(createQueryParam("POSTTYPE", "1"), createTypedQueryParam(PostQueryKeys.POST_TYPE, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 포스트 타입이 대문자로만 (?POSTTYPE=1)이(가) 주어졌을 때 "),
                        Arguments.of(createQueryParam("POSTtYpe", "1"), createTypedQueryParam(PostQueryKeys.POST_TYPE, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 포스트 타입이 대소문자가 섞여 (?POSTtYpe=1)이(가) 주어졌을 때 "),

                        //series
                        Arguments.of(createQueryParam("series", "1"), createTypedQueryParam(PostQueryKeys.SERIES, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 시리즈가 소문자로만 (?series=1)이(가) 주어졌을 때"),
                        Arguments.of(createQueryParam("SERIES", "1"), createTypedQueryParam(PostQueryKeys.SERIES, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 시리즈가 대문자로만 (?SERIES=1)이(가) 주어졌을 때"),
                        Arguments.of(createQueryParam("SERieS", "1"), createTypedQueryParam(PostQueryKeys.SERIES, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 시리즈가 대소문자가 섞여 (?SERieS=1)이(가) 주어졌을 때"),

                        //tag
                        Arguments.of(createQueryParam("tag", "1"), createTypedQueryParam(PostQueryKeys.TAG, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 태그가 소문자로만 (?tag=1)이(가) 주어졌을 때"),
                        Arguments.of(createQueryParam("TAG", "1"), createTypedQueryParam(PostQueryKeys.TAG, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 태그가 대문자로만 (?TAG=1)이(가) 주어졌을 때"),
                        Arguments.of(createQueryParam("TaG", "1"), createTypedQueryParam(PostQueryKeys.TAG, "1", PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 태그가 대소문자가 섞여 (?TaG=1)이(가) 주어졌을 때"),

                        //isOnlyOpenPost
                        Arguments.of(createQueryParam("isOnlyOpenPost", "true"), createTypedQueryParam(PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 공개 여부가 카멜케이스로 (?isOnlyOpenPost=true)이(가) 주어졌을 때"),
                        Arguments.of(createQueryParam("isonlyopenpost", "true"), createTypedQueryParam(PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 공개 여부가 소문자로만 (?isonlyopenpost=true)이(가) 주어졌을 때"),
                        Arguments.of(createQueryParam("ISONLYOPENPOST", "true"), createTypedQueryParam(PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 공개 여부가 대문자로만 (?ISONLYOPENPOST=true)이(가) 주어졌을 때"),
                        Arguments.of(createQueryParam("isOnlyOpenPost", "true"), createTypedQueryParam(PostQueryKeys.IS_ONLY_OPEN_POST, "true"), "올바른 공개 여부가 대소문자가 섞여 (?isOnlyOpenPost=true)이(가) 주어졌을 때")
                );
            }

            @DisplayName("올바른 쿼리 파라미터가 주어지면 해당 포스트를 반환한다.")
            @MethodSource("provideValidQueryParam")
            @ParameterizedTest(name = "{index} : {2} 가 주어졌을 때")
            void givenValidQueryParam_whenGetPosts_thenReturns(MultiValueMap<String, String> queryParam, MultiValueMap<PostQueryKeys, String> processedQueryParam, String message) throws Exception {
                // Given
                createUserContextReturns(userContext, AuthLevel.NONE);

                var validPostItem = createPostSimpleResponseCommonDto();

                var pagedItem = createPageObject(List.of(validPostItem), 0, 20, "createdDate", Sort.Direction.DESC);

                doReturn(pagedItem).when(postService).getAllByQueryParams(processedQueryParam, pagedItem.getPageable());

                // When
                var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post")
                        .params(queryParam));

                // Then
                result.andDo(print())
                        .andExpect(status().isOk());

                createMatcher(validPostItem, result, true);
            }

            private static Stream<Arguments> provideInvalidQueryParam() {
                return Stream.of(
                        //postType
                        Arguments.of(createQueryParam("postType", "0"), "포스트 타입이 1보다 작은 경우"),
                        Arguments.of(createQueryParam("postType", DBTypeSize.INT + 1 + ""), "포스트 타입이 INT 최대값보다 큰 경우"),
                        Arguments.of(createQueryParam("postType", "not number"), "포스트 타입이 숫자가 아닌 경우"),

                        //series
                        Arguments.of(createQueryParam("series", "0"), "시리즈가 1보다 작은 경우"),
                        Arguments.of(createQueryParam("series", DBTypeSize.INT + 1 + ""), "시리즈가 INT 최대값보다 큰 경우"),
                        Arguments.of(createQueryParam("series", "not number"), "시리즈가 숫자가 아닌 경우"),

                        //tag
                        Arguments.of(createQueryParam("tag", "0"), "태그가 1보다 작은 경우"),
                        Arguments.of(createQueryParam("tag", DBTypeSize.INT + 1 + ""), "태그가 INT 최대값보다 큰 경우"),
                        Arguments.of(createQueryParam("tag", "not number"), "태그가 숫자가 아닌 경우"),
                        Arguments.of(createQueryParam("tag", "1", "tag", "1"), "태그가 중복된 경우"),
                        Arguments.of(createQueryParam("tag", "1", "tag", "2", "tag", "3", "tag", "4", "tag", "5", "tag", "6", "tag", "7", "tag", "8", "tag", "9", "tag", "10", "tag", "11"), "태그가 10개 초과인 경우"),

                        //isOnlyOpenPost
                        Arguments.of(createQueryParam("isOnlyOpenPost", "true", "isOnlyOpenPost", "false"), "공개 여부가 중복된 경우"),

                        //keyword
                        Arguments.of(createQueryParam("keyword", "", "searchmode", "title"), "키워드가 빈 문자열인 경우"),
                        Arguments.of(createQueryParam("keyword", "a", "searchmode", "title"), "키워드가 2자 미만인 경우"),
                        Arguments.of(createQueryParam("keyword", "a".repeat(21), "searchmode", "title"), "키워드가 20자를 초과하는 경우"),
                        Arguments.of(createQueryParam("keyword", "1"), "검색 모드가 주어지지 않은 경우"),

                        //searchMode
                        Arguments.of(createQueryParam("keyword", "some", "searchmode", "none"), "검색 모드에 올바르지 않은 값이 주어진 경우"),
                        Arguments.of(createQueryParam("keyword", "some", "searchmode", "title", "searchmode", "content"), "검색 모드가 중복된 경우"),
                        Arguments.of(createQueryParam("searchmode", "title"), "키워드가 주어지지 않은 경우")
                );
            }

            @DisplayName("유효하지 않은 쿼리 파라미터가 주어지면 400 에러를 반환한다.")
            @MethodSource("provideInvalidQueryParam")
            @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
            void givenInvalidQueryParam_whenGetPosts_thenReturns(MultiValueMap<String, String> queryParam, String caseName) throws Exception {
                // Given
                createUserContextReturns(userContext, AuthLevel.NONE);

                // When
                var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post")
                        .params(queryParam));

                // Then
                result.andDo(print())
                        .andExpect(status().isBadRequest());
            }

            private static Stream<Arguments> provideAuthLevel() {
                return Stream.of(
                        Arguments.of(AuthLevel.USER, "USER"),
                        Arguments.of(AuthLevel.NONE, "NONE")
                );
            }

            @DisplayName("관리자가 아닌 사용자가 IsOnlyOpenPost 를 false 로 요청하면, 403 에러를 반환한다.")
            @MethodSource("provideAuthLevel")
            @ParameterizedTest(name = "{index} : {1} 권한으로 조회할 때")
            void givenNotAdmin_whenGetPostsAndIsOnlyOpenPostFalse_thenReturns403(AuthLevel authLevel, String caseName) throws Exception {
                // Given
                createUserContextReturns(userContext, authLevel);

                var queryParam = createQueryParam("isOnlyOpenPost", "false");

                // When
                var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post")
                        .params(queryParam));

                // Then
                result.andDo(print())
                        .andExpect(status().isForbidden());
            }

            @DisplayName("올바르지 않은 페이지 사이즈가 주어지면 400 에러를 반환한다.")
            @Test
            void givenInvalidPageSize_whenGetPosts_thenReturns400() throws Exception {
                // Given
                createUserContextReturns(userContext, AuthLevel.NONE);

                var over100 = 101;
                // When
                var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post?size=" + over100));

                // Then
                result.andDo(print())
                        .andExpect(status().isBadRequest());
            }

            private static Stream<Arguments> provideValidSortKeys() {
                return Stream.of(
                        Arguments.of("id"),
                        Arguments.of("title"),
                        Arguments.of("content"),
                        Arguments.of("isOpen"),
                        Arguments.of("series"),
                        Arguments.of("lastModifiedDate"),
                        Arguments.of("createdDate")
                );
            }

            @DisplayName("올바른 정렬 키가 주어지면 해당 포스트를 반환한다.")
            @MethodSource("provideValidSortKeys")
            @ParameterizedTest(name = "{index} : {0} 가 주어졌을 때")
            void givenValidSortKey_whenGetPosts_thenReturns(String sortKey) throws Exception {
                // Given
                createUserContextReturns(userContext, AuthLevel.NONE);

                var validPostItem = createPostSimpleResponseCommonDto();

                var pagedItem = createPageObject(List.of(validPostItem), 0, 20, sortKey, Sort.Direction.ASC);

                doReturn(pagedItem).when(postService).getAllByQueryParams(createTypedQueryParam(PostQueryKeys.IS_ONLY_OPEN_POST, "true"), pagedItem.getPageable());

                // When
                var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post?sort=" + sortKey));

                // Then
                result.andDo(print())
                        .andExpect(status().isOk());

                createMatcher(validPostItem, result, true);
            }

            @DisplayName("올바르지 않은 정렬 키가 주어지면 400 에러를 반환한다.")
            @Test
            void givenInvalidSortKey_whenGetPosts_thenReturns400() throws Exception {
                // Given
                createUserContextReturns(userContext, AuthLevel.NONE);

                var invalidSortKey = "invalidSortKey";

                // When
                var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post?sort=" + invalidSortKey));

                // Then
                result.andDo(print())
                        .andExpect(status().isBadRequest());
            }
        }

        @DisplayName("getPosts - TITLE 매칭")
        @Nested
        class getPost_title {

            @DisplayName("올바른 키워드와 TITLE 검색 모드가 주어지면 해당 포스트를 반환한다.")
            @Test
            void givenKeywordAndTitleSearchMode_whenGetPosts_thenReturns() throws Exception {
                // Given
                createUserContextReturns(userContext, AuthLevel.NONE);

                var queryParam = createQueryParam("keyword", "some", "searchmode", PostKeywordSearchMode.TITLE.name());

                var processedQueryParam = createTypedQueryParam(PostQueryKeys.IS_ONLY_OPEN_POST, "true");
                var validPostItem = createPostSimpleResponseCommonDto();

                var pagedItem = createPageObject(List.of(validPostItem), 0, 20, "createdDate", Sort.Direction.DESC);

                doReturn(pagedItem).when(postService).getAllByTitleKeyword("some", processedQueryParam, pagedItem.getPageable());

                // When
                var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post")
                        .params(queryParam));

                // Then
                result.andDo(print())
                        .andExpect(status().isOk());

                createMatcher(validPostItem, result, true);
            }

        }

        @DisplayName("getPosts - CONTENT 매칭")
        @Nested
        class getPost_content {

            @DisplayName("올바른 키워드와 CONTENT 검색 모드가 주어지면 해당 포스트를 반환한다.")
            @Test
            void givenKeywordAndContentSearchMode_whenGetPosts_thenReturns() throws Exception {
                // Given
                createUserContextReturns(userContext, AuthLevel.NONE);

                var queryParam = createQueryParam("keyword", "some", "searchmode", PostKeywordSearchMode.CONTENT.name());

                var processedQueryParam = createTypedQueryParam(PostQueryKeys.IS_ONLY_OPEN_POST, "true");
                var validPostItem = createPostSimpleResponseCommonDto();

                var pagedItem = createPageObject(List.of(validPostItem), 0, 20, "createdDate", Sort.Direction.DESC);

                doReturn(pagedItem).when(postService).getAllByContentKeyword("some", processedQueryParam, pagedItem.getPageable());

                // When
                var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post")
                        .params(queryParam));

                // Then
                result.andDo(print())
                        .andExpect(status().isOk());

                createMatcher(validPostItem, result, true);
            }

        }

        @DisplayName("getPosts - MIX 매칭")
        @Nested
        class getPost_mix {

            @DisplayName("올바른 키워드와 MIX 검색 모드가 주어지면 해당 포스트를 반환한다.")
            @Test
            void givenKeywordAndMixSearchMode_whenGetPosts_thenReturns() throws Exception {
                // Given
                createUserContextReturns(userContext, AuthLevel.NONE);

                var queryParam = createQueryParam("keyword", "some", "searchmode", PostKeywordSearchMode.MIX.name());

                var processedQueryParam = createTypedQueryParam(PostQueryKeys.IS_ONLY_OPEN_POST, "true");
                var validPostItem = createPostSimpleResponseCommonDto();

                var pagedItem = createPageObject(List.of(validPostItem), 0, 20, "createdDate", Sort.Direction.DESC);

                doReturn(pagedItem).when(postService).getAllByMixKeyword("some", processedQueryParam, pagedItem.getPageable());

                // When
                var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/post")
                        .params(queryParam));

                // Then
                result.andDo(print())
                        .andExpect(status().isOk());

                createMatcher(validPostItem, result, true);
            }

        }
    }

    @DisplayName("deletePostById")
    @Nested
    class deletePostById {

        @DisplayName("올바른 포스트 ID가 주어지면 해당 포스트를 삭제한다.")
        @Test
        void givenValidPostId_whenDeletePostById_thenReturns() throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validPostId = 1L;

            // When
            var result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/post/{id}", validPostId));

            // Then
            result.andDo(print())
                    .andExpect(status().isNoContent())
                    .andDo(document("post/deletePostById",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Post API")
                                    .summary("포스트를 삭제합니다. (AUTH-LEVEL : ADMIN)")
                                    .pathParameters(
                                            parameterWithName("id").description("삭제할 포스트의 ID")
                                    )
                                    .build())));

            verify(postService).delete(validPostId);
        }

        private static Stream<Arguments> provideInvalidPostId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT 에서 나올 수 없는 수"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 최대값 보다 큰 수")
            );
        }

        @DisplayName("유효하지 않은 포스트 ID가 주어지면 400 에러를 반환한다.")
        @MethodSource("provideInvalidPostId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidPostId_whenDeletePostById_thenReturns(Long invalidPostId, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/post/{id}", invalidPostId));

            // Then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER"),
                    Arguments.of(AuthLevel.NONE, "NONE")
            );
        }

        @DisplayName("관리자가 아닌 사용자가 요청하면, 403 에러를 반환한다.")
        @MethodSource("provideAuthLevel")
        @ParameterizedTest(name = "{index} : {1} 권한으로 조회할 때")
        void givenNotAdmin_whenDeletePostById_thenReturns(AuthLevel authLevel, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, authLevel);

            var validPostId = 1L;

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/post/{id}", validPostId));

            // Then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

    }

    @DisplayName("createPost")
    @Nested
    class createPost {

        @DisplayName("올바른 요청이 주어지면 포스트를 생성한다.")
        @Test
        void givenValidRequest_whenCreatePost_thenReturns() throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validRequest = PostCreateRequestControllerDto.builder()
                    .seriesId(1L)
                    .title("title")
                    .content("content")
                    .tags(List.of(
                            new PostTagId(1L),
                            new PostTagId(2L)
                    ))
                    .isEnabled(true)
                    .postTypeId(1L)
                    .thumbnail("http://springnote.blog")
                    .build();

            var serviceDto = PostCreateRequestServiceDto.builder()
                    .seriesId(1L)
                    .title("title")
                    .content("content")
                    .tagIds(List.of(1L, 2L))
                    .isEnabled(true)
                    .postTypeId(1L)
                    .thumbnail("http://springnote.blog")
                    .build();

            var savedPost = createPostDetailResponseCommonDto();

            doReturn(savedPost).when(postService).create(serviceDto);

            // When
            var result = mockMvc.perform(RestDocumentationRequestBuilders.post("/api/v1/post")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("post/createPost",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Post API")
                                    .summary("포스트를 생성합니다. (AUTH-LEVEL : ADMIN)")
                                    .requestFields(
                                            fieldWithPath("series_id").type(NUMBER).description("포스트의 시리즈 ID, 만약 시리즈가 필요 없는 포스트는 null").optional(),
                                            fieldWithPath("title").type(STRING).description("포스트의 제목, 3~300자"),
                                            fieldWithPath("content").type(STRING).description("포스트의 내용, 3~30000자"),
                                            fieldWithPath("tags").type(ARRAY).description("포스트의 태그 ID 리스트"),
                                            fieldWithPath("tags[].id").type(NUMBER).description("포스트의 태그 ID"),
                                            fieldWithPath("enabled").type(BOOLEAN).description("포스트의 공개 여부"),
                                            fieldWithPath("post_type_id").type(NUMBER).description("포스트의 타입 ID"),
                                            fieldWithPath("thumbnail").type(STRING).description("포스트의 썸네일 URL, 빈 문자열 가능")
                                    )
                                    .responseFields(
                                            postDetail()
                                    )
                                    .requestSchema(Schema.schema("Post.Create"))
                                    .responseSchema(Schema.schema("Post.Detail"))
                                    .build())));


            createMatcher(savedPost, result, false);


            verify(postService).create(serviceDto);
        }


        private static Stream<Arguments> provideInvalidPostRequest() {
            var base = PostCreateRequestControllerDto.builder()
                    .seriesId(1L)
                    .title("title")
                    .content("content")
                    .tags(List.of(
                            new PostTagId(1L),
                            new PostTagId(2L)
                    ))
                    .isEnabled(true)
                    .postTypeId(1L)
                    .thumbnail("http://springnote.blog")
                    .build();

            var tooSmallSeriesId = copyPostCreateRequestControllerDto(base);
            tooSmallSeriesId.setSeriesId(0L);

            var tooLargeSeriesId = copyPostCreateRequestControllerDto(base);
            tooLargeSeriesId.setSeriesId(DBTypeSize.INT + 1);

            var tooSmallTagId = copyPostCreateRequestControllerDto(base);
            tooSmallTagId.setTags(List.of(new PostTagId(0L)));

            var tooLargeTagId = copyPostCreateRequestControllerDto(base);
            tooLargeTagId.setTags(List.of(new PostTagId(DBTypeSize.INT + 1)));

            var tooManyTags = copyPostCreateRequestControllerDto(base);
            tooManyTags.setTags(List.of(
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
            ));

            var tooSmallPostTypeId = copyPostCreateRequestControllerDto(base);
            tooSmallPostTypeId.setPostTypeId(0L);

            var tooLargePostTypeId = copyPostCreateRequestControllerDto(base);
            tooLargePostTypeId.setPostTypeId(DBTypeSize.INT + 1);

            var tooShortTitle = copyPostCreateRequestControllerDto(base);
            tooShortTitle.setTitle("a");

            var tooLongTitle = copyPostCreateRequestControllerDto(base);
            tooLongTitle.setTitle("a".repeat(301));

            var tooShortContent = copyPostCreateRequestControllerDto(base);
            tooShortContent.setContent("a");

            var tooLongContent = copyPostCreateRequestControllerDto(base);
            tooLongContent.setContent("a".repeat(30001));

            var invalidThumbnail = copyPostCreateRequestControllerDto(base);
            invalidThumbnail.setThumbnail("a".repeat(301));

            var nullIsEnabled = copyPostCreateRequestControllerDto(base);
            nullIsEnabled.setIsEnabled(null);

            return Stream.of(
                    Arguments.of(tooSmallSeriesId, "시리즈 ID 가 1보다 작은 경우"),
                    Arguments.of(tooLargeSeriesId, "시리즈 ID 가 INT 최대값보다 큰 경우"),
                    Arguments.of(tooSmallTagId, "태그 ID 가 1보다 작은 경우"),
                    Arguments.of(tooLargeTagId, "태그 ID 가 INT 최대값보다 큰 경우"),
                    Arguments.of(tooManyTags, "태그가 10개 초과인 경우"),
                    Arguments.of(tooSmallPostTypeId, "포스트 타입 ID 가 1보다 작은 경우"),
                    Arguments.of(tooLargePostTypeId, "포스트 타입 ID 가 INT 최대값보다 큰 경우"),
                    Arguments.of(tooShortTitle, "제목이 2자 미만인 경우"),
                    Arguments.of(tooLongTitle, "제목이 300자를 초과하는 경우"),
                    Arguments.of(tooShortContent, "내용이 2자 미만인 경우"),
                    Arguments.of(tooLongContent, "내용이 30000자를 초과하는 경우"),
                    Arguments.of(invalidThumbnail, "썸네일이 300자를 초과하는 경우"),
                    Arguments.of(nullIsEnabled, "공개 여부가 null 인 경우")
            );

        }

        @DisplayName("유효하지 않은 요청이 주어지면 400 에러를 반환한다.")
        @MethodSource("provideInvalidPostRequest")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidRequest_whenCreatePost_thenReturns(PostCreateRequestControllerDto invalidRequest, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/post")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(invalidRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER"),
                    Arguments.of(AuthLevel.NONE, "NONE"));
        }

        @DisplayName("관리자가 아닌 사용자가 요청하면, 403 에러를 반환한다.")
        @MethodSource("provideAuthLevel")
        @ParameterizedTest(name = "{index} : {1} 권한으로 조회할 때")
        void givenNotAdmin_whenCreatePost_thenReturns403(AuthLevel authLevel, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, authLevel);

            var validRequest = PostCreateRequestControllerDto.builder()
                    .seriesId(1L)
                    .title("title")
                    .content("content")
                    .tags(List.of(
                            new PostTagId(1L),
                            new PostTagId(2L)
                    ))
                    .isEnabled(true)
                    .postTypeId(1L)
                    .thumbnail("http://springnote.blog")
                    .build();

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/post")
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @DisplayName("updatePost")
    @Nested
    class updatePost {

        @DisplayName("올바른 요청이 주어지면 포스트를 수정한다.")
        @Test
        void givenValidRequest_whenUpdatePost_thenReturns() throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var validId = 1L;
            var validRequest = PostUpdateRequestControllerDto.builder()
                    .seriesId(1L)
                    .title("title")
                    .content("content")
                    .tags(List.of(
                            new PostTagId(1L),
                            new PostTagId(2L)
                    ))
                    .isEnabled(true)
                    .thumbnail("http://springnote.blog")
                    .build();

            var serviceDto = PostUpdateRequestServiceDto.builder()
                    .id(validId)
                    .seriesId(1L)
                    .title("title")
                    .content("content")
                    .tagIds(List.of(1L, 2L))
                    .seriesId(1L)
                    .isEnabled(true)
                    .thumbnail("http://springnote.blog")
                    .build();

            var updatedPost = createPostDetailResponseCommonDto();

            doReturn(updatedPost).when(postService).update(serviceDto);

            // When
            var result = mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/post/{id}", validId)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("post/updatePost",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Post API")
                                    .summary("포스트를 수정합니다. (AUTH-LEVEL : ADMIN)")
                                    .pathParameters(
                                            parameterWithName("id").description("수정할 포스트의 ID")
                                    )
                                    .requestFields(
                                            fieldWithPath("series_id").type(NUMBER).description("포스트의 시리즈 ID, 만약 시리즈가 필요 없는 포스트는 null").optional(),
                                            fieldWithPath("title").type(STRING).description("포스트의 제목, 3~300자"),
                                            fieldWithPath("content").type(STRING).description("포스트의 내용, 3~30000자"),
                                            fieldWithPath("tags").type(ARRAY).description("포스트의 태그 ID 리스트"),
                                            fieldWithPath("tags[].id").type(NUMBER).description("포스트의 태그 ID"),
                                            fieldWithPath("enabled").type(BOOLEAN).description("포스트의 공개 여부"),
                                            fieldWithPath("thumbnail").type(STRING).description("포스트의 썸네일 URL, 빈 문자열 가능")
                                    )
                                    .responseFields(
                                            postDetail()
                                    )
                                    .requestSchema(Schema.schema("Post.Update"))
                                    .responseSchema(Schema.schema("Post.Detail"))
                                    .build())
                    ));

            createMatcher(updatedPost, result, false);

            verify(postService).update(serviceDto);
        }

        private static Stream<Arguments> provideInvalidPostId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT 에서 나올 수 없는 수"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 최대값 보다 큰 수")
            );
        }

        @DisplayName("올바르지 않은 포스트 Id가 주어지면 400 에러를 반환한다.")
        @MethodSource("provideInvalidPostId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidPostId_whenUpdatePost_thenReturns(Long invalidPostId, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validRequest = PostUpdateRequestControllerDto.builder()
                    .seriesId(1L)
                    .title("title")
                    .content("content")
                    .tags(List.of(
                            new PostTagId(1L),
                            new PostTagId(2L)
                    ))
                    .isEnabled(true)
                    .thumbnail("http://springnote.blog")
                    .build();

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/post/{id}", invalidPostId)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideInvalidPostRequest() {
            var base = PostUpdateRequestControllerDto.builder()
                    .seriesId(1L)
                    .title("title")
                    .content("content")
                    .tags(List.of(
                            new PostTagId(1L),
                            new PostTagId(2L)
                    ))
                    .isEnabled(true)
                    .thumbnail("http://springnote.blog")
                    .build();

            var tooSmallSeriesId = copyPostUpdateRequestControllerDto(base);
            tooSmallSeriesId.setSeriesId(0L);

            var tooLargeSeriesId = copyPostUpdateRequestControllerDto(base);
            tooLargeSeriesId.setSeriesId(DBTypeSize.INT + 1);

            var tooSmallTagId = copyPostUpdateRequestControllerDto(base);
            tooSmallTagId.setTags(List.of(new PostTagId(0L)));

            var tooLargeTagId = copyPostUpdateRequestControllerDto(base);
            tooLargeTagId.setTags(List.of(new PostTagId(DBTypeSize.INT + 1)));

            var tooManyTags = copyPostUpdateRequestControllerDto(base);
            tooManyTags.setTags(List.of(
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
            ));

            var tooShortTitle = copyPostUpdateRequestControllerDto(base);
            tooShortTitle.setTitle("a");

            var tooLongTitle = copyPostUpdateRequestControllerDto(base);
            tooLongTitle.setTitle("a".repeat(301));

            var tooShortContent = copyPostUpdateRequestControllerDto(base);
            tooShortContent.setContent("a");

            var tooLongContent = copyPostUpdateRequestControllerDto(base);
            tooLongContent.setContent("a".repeat(30001));

            var invalidThumbnail = copyPostUpdateRequestControllerDto(base);
            invalidThumbnail.setThumbnail("a".repeat(301));

            var nullIsEnabled = copyPostUpdateRequestControllerDto(base);
            nullIsEnabled.setIsEnabled(null);

            return Stream.of(
                    Arguments.of(tooSmallSeriesId, "시리즈 ID 가 1보다 작은 경우"),
                    Arguments.of(tooLargeSeriesId, "시리즈 ID 가 INT 최대값보다 큰 경우"),
                    Arguments.of(tooSmallTagId, "태그 ID 가 1보다 작은 경우"),
                    Arguments.of(tooLargeTagId, "태그 ID 가 INT 최대값보다 큰 경우"),
                    Arguments.of(tooManyTags, "태그가 10개 초과인 경우"),
                    Arguments.of(tooShortTitle, "제목이 2자 미만인 경우"),
                    Arguments.of(tooLongTitle, "제목이 300자를 초과하는 경우"),
                    Arguments.of(tooShortContent, "내용이 2자 미만인 경우"),
                    Arguments.of(tooLongContent, "내용이 30000자를 초과하는 경우"),
                    Arguments.of(invalidThumbnail, "썸네일이 300자를 초과하는 경우"),
                    Arguments.of(nullIsEnabled, "공개 여부가 null 인 경우")
            );
        }

        @DisplayName("유효하지 않은 요청이 주어지면 400 에러를 반환한다.")
        @MethodSource("provideInvalidPostRequest")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidRequest_whenUpdatePost_thenReturns(PostUpdateRequestControllerDto invalidRequest, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validId = 1L;

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/post/{id}", validId)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(invalidRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER"),
                    Arguments.of(AuthLevel.NONE, "NONE"));
        }

        @DisplayName("관리자가 아닌 사용자가 요청하면, 403 에러를 반환한다.")
        @MethodSource("provideAuthLevel")
        @ParameterizedTest(name = "{index} : {1} 권한으로 조회할 때")
        void givenNotAdmin_whenUpdatePost_thenReturns403(AuthLevel authLevel, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, authLevel);

            var validId = 1L;
            var validRequest = PostUpdateRequestControllerDto.builder()
                    .seriesId(1L)
                    .title("title")
                    .content("content")
                    .tags(List.of(
                            new PostTagId(1L),
                            new PostTagId(2L)
                    ))
                    .isEnabled(true)
                    .thumbnail("http://springnote.blog")
                    .build();

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/post/{id}", validId)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }
    }

    @DisplayName("updatePostStatus")
    @Nested
    class updatePostStatus {

        @DisplayName("올바른 포스트 ID와 요청이 주어지면 포스트의 상태를 수정한다.")
        @Test
        void givenValidPostIdAndRequest_whenUpdatePostStatus_thenReturns() throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validId = 1L;
            var validRequest = PostStatusUpdateRequestControllerDto.builder()
                    .isEnabled(true)
                    .build();

            var serviceDto = PostStatusUpdateRequestServiceDto.builder()
                    .id(validId)
                    .isEnabled(true)
                    .build();

            var updatedPost = createPostDetailResponseCommonDto();

            doReturn(updatedPost).when(postService).updateStatus(serviceDto);

            // When
            var result = mockMvc.perform(RestDocumentationRequestBuilders.put("/api/v1/post/{id}/status", validId)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("post/updatePostStatus",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Post API")
                                    .summary("포스트의 상태를 수정합니다. (AUTH-LEVEL : ADMIN)")
                                    .pathParameters(
                                            parameterWithName("id").description("수정할 포스트의 ID")
                                    )
                                    .requestFields(
                                            fieldWithPath("enabled").type(BOOLEAN).description("포스트의 공개 여부")
                                    )
                                    .responseFields(
                                            postDetail()
                                    )
                                    .requestSchema(Schema.schema("Post.UpdateStatus"))
                                    .responseSchema(Schema.schema("Post.Detail"))
                                    .build())
                    ));

            createMatcher(updatedPost, result, false);

            verify(postService).updateStatus(serviceDto);
        }

        private static Stream<Arguments> provideInvalidPostId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT 에서 나올 수 없는 수"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 최대값 보다 큰 수")
            );
        }

        @DisplayName("올바르지 않은 포스트 ID가 주어지면 400 에러를 반환한다.")
        @MethodSource("provideInvalidPostId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidPostId_whenUpdatePostStatus_thenReturns(Long invalidPostId, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validRequest = PostStatusUpdateRequestControllerDto.builder()
                    .isEnabled(true)
                    .build();

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/post/{id}/status", invalidPostId)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideInvalidPostStatusRequest() {
            var nullIsEnabled = PostStatusUpdateRequestControllerDto.builder()
                    .isEnabled(null)
                    .build();

            return Stream.of(
                    Arguments.of(nullIsEnabled, "공개 여부가 null 인 경우")
            );
        }

        @DisplayName("유효하지 않은 요청이 주어지면 400 에러를 반환한다.")
        @MethodSource("provideInvalidPostStatusRequest")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때")
        void givenInvalidRequest_whenUpdatePostStatus_thenReturns(PostStatusUpdateRequestControllerDto invalidRequest, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validId = 1L;

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/post/{id}/status", validId)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(invalidRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "USER"),
                    Arguments.of(AuthLevel.NONE, "NONE"));
        }

        @DisplayName("관리자가 아닌 사용자가 요청하면, 403 에러를 반환한다.")
        @MethodSource("provideAuthLevel")
        @ParameterizedTest(name = "{index} : {1} 권한으로 조회할 때")
        void givenNotAdmin_whenUpdatePostStatus_thenReturns403(AuthLevel authLevel, String caseName) throws Exception {
            // Given
            createUserContextReturns(userContext, authLevel);

            var validId = 1L;
            var validRequest = PostStatusUpdateRequestControllerDto.builder()
                    .isEnabled(true)
                    .build();

            // When
            var result = mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/post/{id}/status", validId)
                    .contentType("application/json")
                    .content(jsonUtil.toJson(validRequest)));

            // Then
            result.andDo(print())
                    .andExpect(status().isForbidden());
        }

    }
}
