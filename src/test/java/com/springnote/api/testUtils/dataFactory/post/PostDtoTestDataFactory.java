package com.springnote.api.testUtils.dataFactory.post;

import com.springnote.api.domain.post.PostQueryKeys;
import com.springnote.api.dto.content.controller.ContentResponseControllerDto;
import com.springnote.api.dto.post.common.PostDetailResponseCommonDto;
import com.springnote.api.dto.post.common.PostSimpleResponseCommonDto;
import com.springnote.api.dto.post.controller.PostCreateRequestControllerDto;
import com.springnote.api.dto.post.controller.PostUpdateRequestControllerDto;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.dto.series.common.SeriesSimpleResponseDto;
import com.springnote.api.dto.tag.common.TagResponseDto;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class PostDtoTestDataFactory {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static PostDetailResponseCommonDto createPostDetailResponseCommonDto() {
        return PostDetailResponseCommonDto.builder()
                .id(1L)
                .title("title")
                .content(ContentResponseControllerDto.builder()
                        .editorText("editorText")
                        .plainText("plainText")
                        .build()
                )
                .createdAt(testLocalDateTime())
                .lastUpdatedAt(testLocalDateTime())
                .postType(
                        PostTypeResponseDto.builder()
                                .id(1L)
                                .name("name")
                                .build()
                )
                .isEnabled(true)
                .series(
                        SeriesSimpleResponseDto.builder()
                                .id(1L)
                                .name("name")
                                .build()
                )
                .thumbnail("thumbnail")
                .tags(List.of(
                        new TagResponseDto(1L, "tag1"),
                        new TagResponseDto(2L, "tag2")
                ))
                .build();
    }

    public static PostSimpleResponseCommonDto createPostSimpleResponseCommonDto() {
        return PostSimpleResponseCommonDto.builder()
                .id(1L)
                .title("title")
                .createdAt(testLocalDateTime())
                .lastUpdatedAt(testLocalDateTime())
                .series(
                        SeriesSimpleResponseDto.builder()
                                .id(1L)
                                .name("name")
                                .build()
                )
                .thumbnail("thumbnail")
                .tags(List.of(
                        new TagResponseDto(1L, "tag1"),
                        new TagResponseDto(2L, "tag2")
                ))
                .isEnabled(true)
                .postType(
                        PostTypeResponseDto.builder()
                                .id(1L)
                                .name("name")
                                .build()
                )
                .build();
    }

    public static void createMatcher(PostDetailResponseCommonDto expected, ResultActions result, boolean isPageable) throws Exception {
        if (isPageable) {
            result.andExpect(jsonPath("$.posts[0].id").value(expected.getId()))
                    .andExpect(jsonPath("$.posts[0].title").value(expected.getTitle()))
                    .andExpect(jsonPath("$.posts[0].created_at").value(expected.getCreatedAt().format(formatter)))
                    .andExpect(jsonPath("$.posts[0].last_updated_at").value(expected.getLastUpdatedAt().format(formatter)))
                    .andExpect(jsonPath("$.posts[0].enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$.posts[0].thumbnail").value(expected.getThumbnail()))
                    .andExpect(jsonPath("$.posts[0].series.id").value(expected.getSeries().getId()))
                    .andExpect(jsonPath("$.posts[0].series.name").value(expected.getSeries().getName()))
                    .andExpect(jsonPath("$.posts[0].tags[0].id").value(expected.getTags().get(0).getId()))
                    .andExpect(jsonPath("$.posts[0].tags[0].name").value(expected.getTags().get(0).getName()))
                    .andExpect(jsonPath("$.posts[0].content.plain_text").value(expected.getContent().getPlainText()))
                    .andExpect(jsonPath("$.posts[0].content.editor_text").value(expected.getContent().getEditorText()));

        } else {
            result.andExpect(jsonPath("$.id").value(expected.getId()))
                    .andExpect(jsonPath("$.title").value(expected.getTitle()))
                    .andExpect(jsonPath("$.created_at").value(expected.getCreatedAt().format(formatter)))
                    .andExpect(jsonPath("$.last_updated_at").value(expected.getLastUpdatedAt().format(formatter)))
                    .andExpect(jsonPath("$.enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$.thumbnail").value(expected.getThumbnail()))
                    .andExpect(jsonPath("$.series.id").value(expected.getSeries().getId()))
                    .andExpect(jsonPath("$.series.name").value(expected.getSeries().getName()))
                    .andExpect(jsonPath("$.tags[0].id").value(expected.getTags().get(0).getId()))
                    .andExpect(jsonPath("$.tags[0].name").value(expected.getTags().get(0).getName()))
                    .andExpect(jsonPath("$.content.plain_text").value(expected.getContent().getPlainText()))
                    .andExpect(jsonPath("$.content.editor_text").value(expected.getContent().getEditorText()));
        }
    }

    public static void createMatcher(PostSimpleResponseCommonDto expected, ResultActions result, boolean isPageable) throws Exception {
        if (isPageable) {
            result.andExpect(jsonPath("$._embedded.posts[0].id").value(expected.getId()))
                    .andExpect(jsonPath("$._embedded.posts[0].title").value(expected.getTitle()))
                    .andExpect(jsonPath("$._embedded.posts[0].created_at").value(expected.getCreatedAt().format(formatter)))
                    .andExpect(jsonPath("$._embedded.posts[0].last_updated_at").value(expected.getLastUpdatedAt().format(formatter)))
                    .andExpect(jsonPath("$._embedded.posts[0].enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$._embedded.posts[0].thumbnail").value(expected.getThumbnail()))
                    .andExpect(jsonPath("$._embedded.posts[0].series.id").value(expected.getSeries().getId()))
                    .andExpect(jsonPath("$._embedded.posts[0].series.name").value(expected.getSeries().getName()))
                    .andExpect(jsonPath("$._embedded.posts[0].tags[0].id").value(expected.getTags().get(0).getId()))
                    .andExpect(jsonPath("$._embedded.posts[0].tags[0].name").value(expected.getTags().get(0).getName()));

        } else {
            result.andExpect(jsonPath("$.id").value(expected.getId()))
                    .andExpect(jsonPath("$.title").value(expected.getTitle()))
                    .andExpect(jsonPath("$.created_at").value(expected.getCreatedAt().format(formatter)))
                    .andExpect(jsonPath("$.last_updated_at").value(expected.getLastUpdatedAt().format(formatter)))
                    .andExpect(jsonPath("$.enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$.thumbnail").value(expected.getThumbnail()))
                    .andExpect(jsonPath("$.series.id").value(expected.getSeries().getId()))
                    .andExpect(jsonPath("$.series.name").value(expected.getSeries().getName()))
                    .andExpect(jsonPath("$.tags[0].id").value(expected.getTags().get(0).getId()))
                    .andExpect(jsonPath("$.tags[0].name").value(expected.getTags().get(0).getName()));
        }
    }

    // 중간에 쓸모 없는 쿼리파라미터 등을 삭제하는 로직이 있기 때문에 Immutable로 제공하면 안됨
    public static MultiValueMap<String, String> createQueryParam(String k1, String v1) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        return newQueryParam;
    }


    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        newQueryParam.add(k9, v9);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        newQueryParam.add(k9, v9);
        newQueryParam.add(k10, v10);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        newQueryParam.add(k9, v9);
        newQueryParam.add(k10, v10);
        newQueryParam.add(k11, v11);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam(String k1, String v1, String k2, String v2, String k3, String v3, String k4, String v4, String k5, String v5, String k6, String v6, String k7, String v7, String k8, String v8, String k9, String v9, String k10, String v10, String k11, String v11, String k12, String v12) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        newQueryParam.add(k9, v9);
        newQueryParam.add(k10, v10);
        newQueryParam.add(k11, v11);
        newQueryParam.add(k12, v12);
        return newQueryParam;
    }

    public static MultiValueMap<String, String> createQueryParam() {
        return new LinkedMultiValueMap<String, String>();
    }

    public static MultiValueMap<String, String> copyQueryParam(MultiValueMap<String, String> queryParam) {
        var newQueryParam = new LinkedMultiValueMap<String, String>();
        newQueryParam.putAll(queryParam);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3, PostQueryKeys k4, String v4) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3, PostQueryKeys k4, String v4, PostQueryKeys k5, String v5) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3, PostQueryKeys k4, String v4, PostQueryKeys k5, String v5, PostQueryKeys k6, String v6) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3, PostQueryKeys k4, String v4, PostQueryKeys k5, String v5, PostQueryKeys k6, String v6, PostQueryKeys k7, String v7) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3, PostQueryKeys k4, String v4, PostQueryKeys k5, String v5, PostQueryKeys k6, String v6, PostQueryKeys k7, String v7, PostQueryKeys k8, String v8) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3, PostQueryKeys k4, String v4, PostQueryKeys k5, String v5, PostQueryKeys k6, String v6, PostQueryKeys k7, String v7, PostQueryKeys k8, String v8, PostQueryKeys k9, String v9) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        newQueryParam.add(k9, v9);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3, PostQueryKeys k4, String v4, PostQueryKeys k5, String v5, PostQueryKeys k6, String v6, PostQueryKeys k7, String v7, PostQueryKeys k8, String v8, PostQueryKeys k9, String v9, PostQueryKeys k10, String v10) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        newQueryParam.add(k9, v9);
        newQueryParam.add(k10, v10);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3, PostQueryKeys k4, String v4, PostQueryKeys k5, String v5, PostQueryKeys k6, String v6, PostQueryKeys k7, String v7, PostQueryKeys k8, String v8, PostQueryKeys k9, String v9, PostQueryKeys k10, String v10, PostQueryKeys k11, String v11) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        newQueryParam.add(k9, v9);
        newQueryParam.add(k10, v10);
        newQueryParam.add(k11, v11);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam(PostQueryKeys k1, String v1, PostQueryKeys k2, String v2, PostQueryKeys k3, String v3, PostQueryKeys k4, String v4, PostQueryKeys k5, String v5, PostQueryKeys k6, String v6, PostQueryKeys k7, String v7, PostQueryKeys k8, String v8, PostQueryKeys k9, String v9, PostQueryKeys k10, String v10, PostQueryKeys k11, String v11, PostQueryKeys k12, String v12) {
        var newQueryParam = new LinkedMultiValueMap<PostQueryKeys, String>();
        newQueryParam.add(k1, v1);
        newQueryParam.add(k2, v2);
        newQueryParam.add(k3, v3);
        newQueryParam.add(k4, v4);
        newQueryParam.add(k5, v5);
        newQueryParam.add(k6, v6);
        newQueryParam.add(k7, v7);
        newQueryParam.add(k8, v8);
        newQueryParam.add(k9, v9);
        newQueryParam.add(k10, v10);
        newQueryParam.add(k11, v11);
        newQueryParam.add(k12, v12);
        return newQueryParam;
    }

    public static MultiValueMap<PostQueryKeys, String> createTypedQueryParam() {
        return new LinkedMultiValueMap<PostQueryKeys, String>();
    }

    public static PostCreateRequestControllerDto copyPostCreateRequestControllerDto(PostCreateRequestControllerDto postCreateRequestControllerDto) {
        return PostCreateRequestControllerDto.builder()
                .content(postCreateRequestControllerDto.getContent())
                .isEnabled(postCreateRequestControllerDto.getIsEnabled())
                .postTypeId(postCreateRequestControllerDto.getPostTypeId())
                .seriesId(postCreateRequestControllerDto.getSeriesId())
                .tags(postCreateRequestControllerDto.getTags())
                .thumbnail(postCreateRequestControllerDto.getThumbnail())
                .title(postCreateRequestControllerDto.getTitle())
                .build();
    }

    public static PostUpdateRequestControllerDto copyPostUpdateRequestControllerDto(PostUpdateRequestControllerDto postUpdateRequestControllerDto) {
        return PostUpdateRequestControllerDto.builder()
                .content(postUpdateRequestControllerDto.getContent())
                .isEnabled(postUpdateRequestControllerDto.getIsEnabled())
                .seriesId(postUpdateRequestControllerDto.getSeriesId())
                .tags(postUpdateRequestControllerDto.getTags())
                .thumbnail(postUpdateRequestControllerDto.getThumbnail())
                .title(postUpdateRequestControllerDto.getTitle())
                .build();
    }
}
