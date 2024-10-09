package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.dto.image.controller.ImageCreateRequestControllerDto;
import com.springnote.api.dto.image.service.ImageCreateRequestServiceDto;
import com.springnote.api.service.ImageService;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.web.controller.ImageApiController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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
import static com.springnote.api.testUtils.dataFactory.image.ImageDtoTestDataFactory.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Controller Test - ImageApiController")
@WebMvcTest(ImageApiController.class)
public class ImageApiControllerTest extends ControllerTestTemplate {

    @Autowired
    private ImageApiController imageApiController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ImageService imageService;

    @DisplayName("uploadImage")
    @Nested
    class uploadImage {

        @DisplayName("정상적인 이미지 업로드 요청이 주어지면, 이미지를 업로드한다.")
        @Test
        void givenValidImageUploadRequest_whenRequested_thenUploadImage() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var request = ImageCreateRequestControllerDto.builder()
                    .image(generateBase64(100))
                    .postId(null)
                    .build();

            var serviceDto = ImageCreateRequestServiceDto.builder()
                    .image(request.getImage())
                    .postId(request.getPostId())
                    .build();

            var savedImage = createImageResponseCommonDto();

            doReturn(savedImage).when(imageService).create(serviceDto);

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/v1/image")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then

            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.message").value(savedImage.getConvertedName()))
                    .andDo(document("image/uploadImage",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Image API")
                                    .summary("이미지를 업로드합니다. (AUTH-LEVEL : ADMIN)")
                                    .requestFields(
                                            fieldWithPath("image").type(STRING).description("이미지 파일 (BASE64 인코딩,5MB 이하)"),
                                            fieldWithPath("postId").type(NUMBER).description("사용중인 포스트").optional()
                                    )
                                    .responseFields(
                                            fieldWithPath("message").type(STRING).description("업로드된 이미지 이름")
                                    )
                                    .requestSchema(Schema.schema("Image.CreateRequest"))
                                    .responseSchema(Schema.schema("Image.Response"))
                                    .build())
                    ));
        }

        private static Stream<Arguments> provideInvalidImageUploadRequest() {
            var notBase64 = ImageCreateRequestControllerDto.builder()
                    .image("notBase64")
                    .postId(null)
                    .build();

            var tooLargeImage = ImageCreateRequestControllerDto.builder()
                    .image(generateBase64(5592406))
                    .postId(null)
                    .build();

            var tooSmallPostId = ImageCreateRequestControllerDto.builder()
                    .image(generateBase64(100))
                    .postId(0L)
                    .build();

            var tooLargePostId = ImageCreateRequestControllerDto.builder()
                    .image(generateBase64(100))
                    .postId(DBTypeSize.INT + 1L)
                    .build();

            var noImage = ImageCreateRequestControllerDto.builder()
                    .image("")
                    .postId(null)
                    .build();

            return Stream.of(
                    Arguments.of(notBase64, "Base64 형식이 아닌 이미지"),
                    Arguments.of(tooLargeImage, "5MB를 초과하는 이미지 (변환 전 용량 기준)"),
                    Arguments.of(tooSmallPostId, "포스트 Id가 AUTO_INCREMENT에서 나올 수 없는 값"),
                    Arguments.of(tooLargePostId, "포스트 ID 가 INT 범위를 초과"),
                    Arguments.of(noImage, "아에 이미지가 없는 경우")
            );
        }

        @DisplayName("비정상적인 이미지 업로드 요청이 주어지면, 에러를 반환한다.")
        @MethodSource("provideInvalidImageUploadRequest")
        @ParameterizedTest(name = "{index} : {1}")
        void givenInvalidImageUploadRequest_whenRequested_thenReturnError(ImageCreateRequestControllerDto request, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/image")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then
            result.andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "유저 권한"),
                    Arguments.of(AuthLevel.NONE, "비인증 상태")
            );
        }

        @DisplayName("권한이 없는 사용자가 이미지 업로드 요청을 하면, 에러를 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}")
        void givenInvalidAuthLevel_whenRequested_thenReturnError(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            var request = ImageCreateRequestControllerDto.builder()
                    .image(generateBase64(100))
                    .postId(null)
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/image")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then
            result.andExpect(status().isForbidden());
        }
    }

    @DisplayName("deleteImage")
    @Nested
    class deleteImage {

        @DisplayName("정상적인 이미지 삭제 요청이 주어지면, 이미지를 삭제한다.")
        @Test
        void givenValidImageDeleteRequest_whenRequested_thenDeleteImage() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var imageId = 1L;

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.delete("/api/v1/image/{imageId}", imageId)
            );

            // then
            result.andExpect(status().isNoContent())
                    .andDo(document("image/deleteImage",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Image API")
                                    .summary("이미지를 삭제합니다. (AUTH-LEVEL : ADMIN)")
                                    .pathParameters(
                                            parameterWithName("imageId").description("삭제할 이미지 ID")
                                    )
                                    .build())
                    ));
        }

        private static Stream<Arguments> provideInvalidImageId() {
            var tooSmallImageId = 0L;
            var tooLargeImageId = DBTypeSize.INT + 1L;

            return Stream.of(
                    Arguments.of(tooSmallImageId, "이미지 ID가 AUTO_INCREMENT에서 나올 수 없는 값"),
                    Arguments.of(tooLargeImageId, "이미지 ID 가 INT 범위를 초과")
            );
        }

        @DisplayName("비정상적인 이미지 삭제 요청이 주어지면, 에러를 반환한다.")
        @MethodSource("provideInvalidImageId")
        @ParameterizedTest(name = "{index} : {1}")
        void givenInvalidImageDeleteRequest_whenRequested_thenReturnError(Long imageId, String description) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/image/" + imageId)
            );

            // then
            result.andExpect(status().isBadRequest());


        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "유저 권한"),
                    Arguments.of(AuthLevel.NONE, "비인증 상태")
            );
        }

        @DisplayName("권한이 없는 사용자가 이미지 삭제 요청을 하면, 에러를 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}")
        void givenInvalidAuthLevel_whenRequested_thenReturnError(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            var imageId = 1L;

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.delete("/api/v1/image/" + imageId)
            );

            // then
            result.andExpect(status().isForbidden());
        }
    }

    @DisplayName("getImages")
    @Nested
    class getImages {

        @DisplayName("모든 이미지를 조회하면, 모든 이미지를 반환한다.")
        @Test
        void givenValidRequest_whenRequested_thenReturnAllImages() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var image = createImageResponseCommonDto();
            var pagedImage = createPageObject(List.of(image), 0, 20, "createdAt", Sort.Direction.DESC);
            doReturn(pagedImage).when(imageService).getAll(pagedImage.getPageable());

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.get("/api/v1/image")
            );

            // then
            result.andExpect(status().isOk())
                    .andDo(document("image/getImages",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Image API")
                                    .summary("업로드된 이미지 정보를 조회합니다. (AUTH-LEVEL : ADMIN)")
                                    .description("* 페이징\n" +
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
                                            "|        id        |     ID   |\n" +
                                            "|      postId      |  포스트 ID  |\n" +
                                            "|     createdAt    |  생성일 |\n" +
                                            "\n")
                                    .responseFields(
                                            fieldWithPath("content[].id").type(NUMBER).description("이미지 ID"),
                                            fieldWithPath("content[].converted_name").type(STRING).description("변환된 이미지 이름"),
                                            fieldWithPath("content[].width").type(NUMBER).description("이미지 너비"),
                                            fieldWithPath("content[].height").type(NUMBER).description("이미지 높이"),
                                            fieldWithPath("content[].created_at").type(STRING).description("생성 시간"),
                                            fieldWithPath("content[].format").type(STRING).description("이미지 형식"),
                                            fieldWithPath("content[].post_id").type(NUMBER).description("사용중인 포스트 ID"),
                                            subsectionWithPath("content").type(ARRAY).description("이미지 목록"),
                                            subsectionWithPath("pageable").ignored(),
                                            subsectionWithPath("sort").ignored(),
                                            subsectionWithPath("totalPages").ignored(),
                                            subsectionWithPath("totalElements").ignored(),
                                            subsectionWithPath("last").ignored(),
                                            subsectionWithPath("size").ignored(),
                                            subsectionWithPath("number").ignored(),
                                            subsectionWithPath("numberOfElements").ignored(),
                                            subsectionWithPath("first").ignored(),
                                            subsectionWithPath("empty").ignored()
                                    )
                                    .responseSchema(Schema.schema("Image.PageResponse"))
                                    .build())
                    ));
            createMatcher(image, result, true);
        }

        private static Stream<Arguments> provideInvalidAuthLevel() {
            return Stream.of(
                    Arguments.of(AuthLevel.USER, "유저 권한"),
                    Arguments.of(AuthLevel.NONE, "비인증 상태")
            );
        }

        @DisplayName("권한이 없는 사용자가 이미지 조회 요청을 하면, 에러를 반환한다.")
        @MethodSource("provideInvalidAuthLevel")
        @ParameterizedTest(name = "{index} : {1}")
        void givenInvalidAuthLevel_whenRequested_thenReturnError(AuthLevel authLevel, String description) throws Exception {
            // given
            createUserContextReturns(userContext, authLevel);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/image")
            );

            // then
            result.andExpect(status().isForbidden());
        }

        private static Stream<Arguments> provideValidSortKey() {
            return Stream.of(
                    Arguments.of("createdAt"),
                    Arguments.of("id"),
                    Arguments.of("postId")
            );
        }

        @DisplayName("정렬 키가 주어지면, 해당 키로 정렬된 이미지를 반환한다.")
        @MethodSource("provideValidSortKey")
        @ParameterizedTest(name = "{index} : {0} 키로 정렬하면, 정상적으로 반환된다.")
        void givenValidSortKey_whenRequested_thenReturnSortedImages(String sortKey) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var image = createImageResponseCommonDto();
            var pagedImage = createPageObject(List.of(image), 0, 20, sortKey, Sort.Direction.ASC);
            doReturn(pagedImage).when(imageService).getAll(pagedImage.getPageable());

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/image")
                            .param("sort", sortKey)
            );

            // then
            result.andExpect(status().isOk());
            createMatcher(image, result, true);
        }

        @DisplayName("올바르지 않은 정렬 키가 주어지면, 에러를 반환한다.")
        @Test
        void givenInvalidSortKey_whenRequested_thenReturnError() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/image")
                            .param("sort", "invalidKey")
            );

            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("올바르지 않은 사이즈가 주어지면, 에러를 반환한다.")
        @Test
        void givenInvalidSize_whenRequested_thenReturnError() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);
            var invalidSize = "101";
            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.get("/api/v1/image")
                            .param("size", invalidSize)
            );

            // then
            result.andExpect(status().isBadRequest());
        }
    }

}

