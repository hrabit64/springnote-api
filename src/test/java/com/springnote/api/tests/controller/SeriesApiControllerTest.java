package com.springnote.api.tests.controller;

import com.epages.restdocs.apispec.ResourceSnippetParameters;
import com.epages.restdocs.apispec.Schema;
import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.dto.assembler.series.SeriesResponseDtoAssembler;
import com.springnote.api.dto.series.controller.SeriesCreateRequestControllerDto;
import com.springnote.api.dto.series.controller.SeriesUpdateRequestControllerDto;
import com.springnote.api.dto.series.service.SeriesCreateRequestServiceDto;
import com.springnote.api.dto.series.service.SeriesUpdateRequestServiceDto;
import com.springnote.api.service.SeriesService;
import com.springnote.api.testUtils.template.ControllerTestTemplate;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.web.controller.SeriesApiController;
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
import static com.springnote.api.testUtils.dataFactory.series.SeriesDtoTestDataFactory.createSeriesResponseCommonDto;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SeriesApiController.class)
public class SeriesApiControllerTest extends ControllerTestTemplate {

    @Autowired
    private SeriesApiController seriesApiController;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SeriesService seriesService;

    @SpyBean
    private SeriesResponseDtoAssembler assembler;

    @DisplayName("getSeriesById")
    @Nested
    class getSeriesById {

        @DisplayName("올바른 시리즈 ID를 입력하면 해당 시리즈를 반환한다.")
        @Test
        void getSeriesById_successWithValidId() throws Exception {
            // given
            var validId = 1L;
            var series = createSeriesResponseCommonDto();

            doReturn(series).when(seriesService).getSeriesById(validId);
            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/series/{seriesId}", validId));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(series.getId()))
                    .andExpect(jsonPath("$.name").value(series.getName()))
                    .andExpect(jsonPath("$.description").value(series.getDescription()))
                    .andExpect(jsonPath("$.thumbnail").value(series.getThumbnail()))
                    .andDo(document("series/getSeriesById",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Series API")
                                    .summary("주어진 Id에 해당하는 시리즈를 조회합니다.")
                                    .pathParameters(
                                            parameterWithName("seriesId").description("조회할 시리즈의 ID")
                                    )
                                    .responseFields(
                                            fieldWithPath("id").description("시리즈 ID"),
                                            fieldWithPath("name").description("시리즈 이름"),
                                            fieldWithPath("description").description("시리즈 설명"),
                                            fieldWithPath("thumbnail").description("시리즈 썸네일"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .responseSchema(Schema.schema("Series.Response"))
                                    .build())));

            verify(seriesService).getSeriesById(validId);
        }

        private static Stream<Arguments> provideInvalidId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT 에서 나올 수 없는 ID"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 범위를 벗어난 ID")
            );
        }

        @DisplayName("유효하지 않은 시리즈 ID를 입력하면 400 에러를 반환한다.")
        @MethodSource("provideInvalidId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때 400 에러를 반환한다.")
        void getSeriesById_failWithInvalidId(Long invalidId, String message) throws Exception {
            // given
            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/series/" + invalidId));

            // then
            result.andExpect(status().isBadRequest());
        }
    }

    @DisplayName("getSeries")
    @Nested
    class getSeries {

        @DisplayName("시리즈 이름을 입력하지 않으면 모든 시리즈를 반환한다.")
        @Test
        void getSeries_successWithNoName() throws Exception {
            // given
            var series = createSeriesResponseCommonDto();
            var pageable = createPageObject(List.of(series), 0, 20, "id", Sort.Direction.DESC);
            var targetPageable = pageable.getPageable();

            doReturn(pageable).when(seriesService).getSeries(targetPageable);

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.get("/api/v1/series"));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.series[0].id").value(series.getId()))
                    .andExpect(jsonPath("$._embedded.series[0].name").value(series.getName()))
                    .andExpect(jsonPath("$._embedded.series[0].description").value(series.getDescription()))
                    .andExpect(jsonPath("$._embedded.series[0].thumbnail").value(series.getThumbnail()))
                    .andDo(document("series/getSeries",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Series API")
                                    .summary("시리즈를 조회합니다.")
                                    .description(
                                            "* 사용 가능 쿼리 파라미터 (대소문자 구분 없음)\n" +
                                                    "\n" +
                                                    "|       키       |                             설명                             |                 제약                 |                기본값                |\n" +
                                                    "| :------------: | :----------------------------------------------------------: | :----------------------------------: | :----------------------------------: |\n" +
                                                    "|    name    |                       검색할 이름.                             |                2~40자                   |                  x                   |\n" +
                                                    "\n" +
                                                    "* 페이징\n" +
                                                    "\n" +
                                                    "해당 API는 페이징을 지원합니다.\n" +
                                                    "\n" +
                                                    "|  키  |          설명           |            제약            |      기본값      |\n" +
                                                    "| :--: | :---------------------: | :------------------------: | :--------------: |\n" +
                                                    "| size |      페이징 사이즈      |        최대 50까지        |        20        |\n" +
                                                    "| page | 페이지 넘버 *0부터 시작 |                            |        0         |\n" +
                                                    "| sort |        정렬 옵션        | *아래 사용 가능 키만 가능* | id;DESC |\n" +
                                                    "\n" +
                                                    "사용 가능한 sort키는 아래와 같습니다. *모든 Sort 사용시 방향을 지정하지 않으면 ASC 로 동작합니다. (대소문자 구분 없음)*\n" +
                                                    "\n" +
                                                    "|        키        |     설명      |\n" +
                                                    "| :--------------: | :-----------: |\n" +
                                                    "|        id        |   시리즈 ID    |\n" +
                                                    "|      name       |  시리즈 제목  |\n" +
                                                    "\n"
                                    )
                                    .responseFields(
                                            fieldWithPath("_embedded.series[].id").description("시리즈 ID"),
                                            fieldWithPath("_embedded.series[].name").description("시리즈 이름"),
                                            fieldWithPath("_embedded.series[].description").description("시리즈 설명"),
                                            fieldWithPath("_embedded.series[].thumbnail").description("시리즈 썸네일"),
                                            subsectionWithPath("_embedded.series[]._links").ignored(),
                                            subsectionWithPath("_links").ignored(),
                                            fieldWithPath("page.size").description("페이지 사이즈"),
                                            fieldWithPath("page.totalElements").description("전체 요소 수"),
                                            fieldWithPath("page.totalPages").description("전체 페이지 수"),
                                            fieldWithPath("page.number").description("현재 페이지 번호")
                                    )
                                    .responseSchema(Schema.schema("Series.PagedResponse"))
                                    .build())));

            verify(seriesService).getSeries(targetPageable);
        }

        @DisplayName("시리즈 이름을 입력하면 해당 이름을 가진 시리즈를 반환한다.")
        @Test
        void getSeries_successWithName() throws Exception {
            // given
            var series = createSeriesResponseCommonDto();
            var pageable = createPageObject(List.of(series), 0, 20, "id", Sort.Direction.DESC);
            var targetPageable = pageable.getPageable();

            doReturn(pageable).when(seriesService).getSeriesByName(series.getName(), targetPageable);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/series?name=" + series.getName()));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.series[0].id").value(series.getId()))
                    .andExpect(jsonPath("$._embedded.series[0].name").value(series.getName()))
                    .andExpect(jsonPath("$._embedded.series[0].description").value(series.getDescription()))
                    .andExpect(jsonPath("$._embedded.series[0].thumbnail").value(series.getThumbnail()));

            verify(seriesService).getSeriesByName(series.getName(), targetPageable);
        }

        private static Stream<Arguments> provideInvalidName() {
            return Stream.of(
                    Arguments.of("", "빈 문자열"),
                    Arguments.of("a", "2글자 미만"),
                    Arguments.of("a".repeat(41), "40글자 초과")
            );
        }

        @DisplayName("유효하지 않은 시리즈 이름을 입력하면 400 에러를 반환한다.")
        @MethodSource("provideInvalidName")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때 400 에러를 반환한다.")
        void getSeries_failWithInvalidName(String invalidName, String message) throws Exception {
            // given

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/series?name=" + invalidName));

            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("페이지 사이즈가 50을 초과하면 400 에러를 반환한다.")
        @Test
        void getSeries_failWithPageSizeOver50() throws Exception {
            // given
            var overPageSize = 51;

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/series?size=" + overPageSize));

            // then
            result.andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideValidSortKey() {
            return Stream.of(
                    Arguments.of("id", "ID"),
                    Arguments.of("name", "이름")
            );
        }

        @DisplayName("유효한 정렬 키를 입력하면 해당 키로 정렬된 시리즈를 반환한다.")
        @MethodSource("provideValidSortKey")
        @ParameterizedTest(name = "{index} : {1} 로 정렬된 시리즈를 반환한다.")
        void getSeries_successWithValidSortKey(String validSortKey, String message) throws Exception {
            // given
            var series = createSeriesResponseCommonDto();
            var pageable = createPageObject(List.of(series), 0, 20, validSortKey, Sort.Direction.ASC);
            var targetPageable = pageable.getPageable();

            doReturn(pageable).when(seriesService).getSeries(targetPageable);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/series?sort=" + validSortKey));

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$._embedded.series[0].id").value(series.getId()))
                    .andExpect(jsonPath("$._embedded.series[0].name").value(series.getName()))
                    .andExpect(jsonPath("$._embedded.series[0].description").value(series.getDescription()))
                    .andExpect(jsonPath("$._embedded.series[0].thumbnail").value(series.getThumbnail()));

            verify(seriesService).getSeries(targetPageable);
        }

        @DisplayName("유효하지 않은 정렬 키를 입력하면 400 에러를 반환한다.")
        @Test
        void getSeries_failWithInvalidSortKey() throws Exception {
            // given
            var invalidSortKey = "invalid";

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/series?sort=" + invalidSortKey));

            // then
            result.andExpect(status().isBadRequest());
        }

    }

    @DisplayName("createSeries")
    @Nested
    class createSeries {

        @DisplayName("올바른 시리즈 정보를 입력하면 시리즈를 생성한다.")
        @Test
        void createSeries_successWithValidRequest() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var request = SeriesCreateRequestControllerDto.builder()
                    .name("test")
                    .description("test")
                    .thumbnail("https://springnote.blog")
                    .build();

            var serviceDto = SeriesCreateRequestServiceDto.builder()
                    .name(request.getName())
                    .description(request.getDescription())
                    .thumbnail("https://springnote.blog")
                    .build();

            var savedSeries = createSeriesResponseCommonDto();

            doReturn(savedSeries).when(seriesService).create(serviceDto);

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/v1/series")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(savedSeries.getId()))
                    .andExpect(jsonPath("$.name").value(savedSeries.getName()))
                    .andExpect(jsonPath("$.description").value(savedSeries.getDescription()))
                    .andExpect(jsonPath("$.thumbnail").value(savedSeries.getThumbnail()))
                    .andDo(document("series/createSeries",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Series API")
                                    .summary("시리즈를 생성합니다. (AUTH_LEVEL: ADMIN)")
                                    .requestFields(
                                            fieldWithPath("name").description("시리즈 이름"),
                                            fieldWithPath("description").description("시리즈 설명"),
                                            fieldWithPath("thumbnail").description("시리즈 썸네일").optional()
                                    )
                                    .responseFields(
                                            fieldWithPath("id").description("시리즈 ID"),
                                            fieldWithPath("name").description("시리즈 이름"),
                                            fieldWithPath("description").description("시리즈 설명"),
                                            fieldWithPath("thumbnail").description("시리즈 썸네일"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .requestSchema(Schema.schema("Series.CreateRequest"))
                                    .responseSchema(Schema.schema("Series.Response"))
                                    .build())));

            verify(seriesService).create(serviceDto);
        }

        private static Stream<Arguments> provideInvalidRequest() {
            var tooShortName = SeriesCreateRequestControllerDto.builder()
                    .name("a")
                    .build();

            var tooLongName = SeriesCreateRequestControllerDto.builder()
                    .name("a".repeat(101))
                    .build();

            var tooLongDescription = SeriesCreateRequestControllerDto.builder()
                    .description("a".repeat(501))
                    .build();

            var invalidThumbnail = SeriesCreateRequestControllerDto.builder()
                    .name("test")
                    .thumbnail("invalid")
                    .build();

            return Stream.of(
                    Arguments.of(tooShortName, "제목이 2글자 미만"),
                    Arguments.of(tooLongName, "제목이 100글자 초과"),
                    Arguments.of(tooLongDescription, "설명이 500글자 초과"),
                    Arguments.of(invalidThumbnail, "썸네일 주소가 올바르지 않음")
            );
        }

        @DisplayName("유효하지 않은 시리즈 정보를 입력하면 400 에러를 반환한다.")
        @MethodSource("provideInvalidRequest")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때 400 에러를 반환한다.")
        void createSeries_failWithInvalidRequest(SeriesCreateRequestControllerDto invalidRequest, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/series")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(invalidRequest))
            );

            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("권한이 없으면 403 에러를 반환한다.")
        @Test
        void createSeries_failWithNoPermission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);

            var request = SeriesCreateRequestControllerDto.builder()
                    .name("test")
                    .description("test")
                    .thumbnail("https://springnote.blog")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.post("/api/v1/series")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then
            result.andExpect(status().isForbidden());

        }
    }

    @DisplayName("updateSeries")
    @Nested
    class updateSeries {
        @DisplayName("올바른 시리즈 정보를 입력하면 시리즈를 수정한다.")
        @Test
        void updateSeries_successWithValidRequest() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validId = 1L;
            var request = SeriesUpdateRequestControllerDto.builder()
                    .name("test")
                    .description("test")
                    .thumbnail("https://springnote.blog")
                    .build();

            var serviceDto = SeriesUpdateRequestServiceDto.builder()
                    .id(validId)
                    .name(request.getName())
                    .description(request.getDescription())
                    .thumbnail("https://springnote.blog")
                    .build();

            var updatedSeries = createSeriesResponseCommonDto();

            doReturn(updatedSeries).when(seriesService).update(serviceDto);

            // when
            var result = mockMvc.perform(
                    RestDocumentationRequestBuilders.put("/api/v1/series/{seriesId}", validId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then
            result.andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(updatedSeries.getId()))
                    .andExpect(jsonPath("$.name").value(updatedSeries.getName()))
                    .andExpect(jsonPath("$.description").value(updatedSeries.getDescription()))
                    .andExpect(jsonPath("$.thumbnail").value(updatedSeries.getThumbnail()))
                    .andDo(document("series/updateSeries",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Series API")
                                    .summary("시리즈를 수정합니다. (AUTH_LEVEL: ADMIN)")
                                    .pathParameters(
                                            parameterWithName("seriesId").description("수정할 시리즈의 ID")
                                    )
                                    .requestFields(
                                            fieldWithPath("name").description("시리즈 이름"),
                                            fieldWithPath("description").description("시리즈 설명"),
                                            fieldWithPath("thumbnail").description("시리즈 썸네일").optional()
                                    )
                                    .responseFields(
                                            fieldWithPath("id").description("시리즈 ID"),
                                            fieldWithPath("name").description("시리즈 이름"),
                                            fieldWithPath("description").description("시리즈 설명"),
                                            fieldWithPath("thumbnail").description("시리즈 썸네일"),
                                            subsectionWithPath("_links").ignored()
                                    )
                                    .requestSchema(Schema.schema("Series.UpdateRequest"))
                                    .responseSchema(Schema.schema("Series.Response"))
                                    .build())));

            verify(seriesService).update(serviceDto);
        }


        private static Stream<Arguments> provideInvalidId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT 에서 나올 수 없는 ID"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 범위를 벗어난 ID")
            );
        }

        @DisplayName("유효하지 않은 시리즈 ID를 입력하면 400 에러를 반환한다.")
        @MethodSource("provideInvalidId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때 400 에러를 반환한다.")
        void updateSeries_failWithInvalidId(Long invalidId, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var request = SeriesUpdateRequestControllerDto.builder()
                    .name("test")
                    .description("test")
                    .thumbnail("https://springnote.blog")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/series/" + invalidId)
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then
            result.andExpect(status().isBadRequest());
        }

        private static Stream<Arguments> provideInvalidRequest() {
            var tooShortName = SeriesUpdateRequestControllerDto.builder()
                    .name("a")
                    .build();

            var tooLongName = SeriesUpdateRequestControllerDto.builder()
                    .name("a".repeat(101))
                    .build();

            var tooLongDescription = SeriesUpdateRequestControllerDto.builder()
                    .description("a".repeat(501))
                    .build();

            var invalidThumbnail = SeriesUpdateRequestControllerDto.builder()
                    .name("test")
                    .thumbnail("invalid")
                    .build();

            return Stream.of(
                    Arguments.of(tooShortName, "제목이 2글자 미만"),
                    Arguments.of(tooLongName, "제목이 100글자 초과"),
                    Arguments.of(tooLongDescription, "설명이 500글자 초과"),
                    Arguments.of(invalidThumbnail, "썸네일 주소가 올바르지 않음")
            );
        }

        @DisplayName("유효하지 않은 시리즈 정보를 입력하면 400 에러를 반환한다.")
        @MethodSource("provideInvalidRequest")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때 400 에러를 반환한다.")
        void updateSeries_failWithInvalidRequest(SeriesUpdateRequestControllerDto invalidRequest, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/series/1")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(invalidRequest))
            );

            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("권한이 없으면 403 에러를 반환한다.")
        @Test
        void updateSeries_failWithNoPermission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);

            var request = SeriesUpdateRequestControllerDto.builder()
                    .name("test")
                    .description("test")
                    .thumbnail("https://springnote.blog")
                    .build();

            // when
            var result = mockMvc.perform(
                    MockMvcRequestBuilders.put("/api/v1/series/1")
                            .contentType("application/json")
                            .content(jsonUtil.toJson(request))
            );

            // then
            result.andExpect(status().isForbidden());
        }

    }

    @DisplayName("deleteSeries")
    @Nested
    class deleteSeries {

        @DisplayName("올바른 시리즈 ID를 입력하면 시리즈를 삭제한다.")
        @Test
        void deleteSeries_successWithValidId() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            var validId = 1L;

            // when
            var result = mockMvc.perform(RestDocumentationRequestBuilders.delete("/api/v1/series/{seriesId}", validId));

            // then
            result.andExpect(status().isNoContent())
                    .andDo(document("series/deleteSeries",
                            preprocessRequest(prettyPrint()),
                            preprocessResponse(prettyPrint()),
                            resource(ResourceSnippetParameters.builder()
                                    .tag("Series API")
                                    .summary("시리즈를 삭제합니다. (AUTH_LEVEL: ADMIN)")
                                    .description("주어진 id에 해당하는 시리즈를 삭제합니다. 이때 해당 시리즈에 속한 모든 포스트도 삭제됩니다.")
                                    .pathParameters(
                                            parameterWithName("seriesId").description("삭제할 시리즈의 ID")
                                    )
                                    .build())));

            verify(seriesService).delete(validId);
        }

        private static Stream<Arguments> provideInvalidId() {
            return Stream.of(
                    Arguments.of(0L, "AUTO_INCREMENT 에서 나올 수 없는 ID"),
                    Arguments.of(DBTypeSize.INT + 1, "INT 범위를 벗어난 ID")
            );
        }

        @DisplayName("유효하지 않은 시리즈 ID를 입력하면 400 에러를 반환한다.")
        @MethodSource("provideInvalidId")
        @ParameterizedTest(name = "{index} : {1} 가 주어졌을 때 400 에러를 반환한다.")
        void deleteSeries_failWithInvalidId(Long invalidId, String message) throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.ADMIN);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/series/" + invalidId));

            // then
            result.andExpect(status().isBadRequest());
        }

        @DisplayName("권한이 없으면 403 에러를 반환한다.")
        @Test
        void deleteSeries_failWithNoPermission() throws Exception {
            // given
            createUserContextReturns(userContext, AuthLevel.USER);

            // when
            var result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/series/1"));

            // then
            result.andExpect(status().isForbidden());
        }

    }
}
