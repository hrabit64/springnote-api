package com.springnote.api.testUtils.docs;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.JsonFieldType.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;

public class PostResponseFieldGenerator {
    public static FieldDescriptor[] postDetail() {
        return new FieldDescriptor[]{
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
        };
    }

    public static FieldDescriptor[] postSimple() {
        return new FieldDescriptor[]{
                subsectionWithPath("_links").ignored(),
                fieldWithPath("id").type(NUMBER).description("대상 포스트 ID"),
                fieldWithPath("title").type(STRING).description("포스트 제목"),
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
        };
    }

    public static FieldDescriptor[] postPagedSimple() {
        return new FieldDescriptor[]{
                subsectionWithPath("_links").ignored(),
                subsectionWithPath("_embedded.posts[]._links").ignored(),
                fieldWithPath("page.size").type(NUMBER).description("페이지 크기"),
                fieldWithPath("page.totalElements").type(NUMBER).description("전체 요소 수"),
                fieldWithPath("page.totalPages").type(NUMBER).description("전체 페이지 수"),
                fieldWithPath("page.number").type(NUMBER).description("현재 페이지 번호"),
                fieldWithPath("_embedded.posts[].id").type(NUMBER).description("대상 포스트 ID"),
                fieldWithPath("_embedded.posts[].title").type(STRING).description("포스트 제목"),
                fieldWithPath("_embedded.posts[].created_at").type(STRING).description("포스트 생성일 (yyyy-MM-dd`T`HH:mm:ss)"),
                fieldWithPath("_embedded.posts[].last_updated_at").type(STRING).description("포스트 최종 수정일 (yyyy-MM-dd`T`HH:mm:ss)"),
                fieldWithPath("_embedded.posts[].post_type.id").type(NUMBER).description("포스트 타입 ID"),
                fieldWithPath("_embedded.posts[].post_type.name").type(STRING).description("포스트 타입 이름"),
                fieldWithPath("_embedded.posts[].enabled").type(BOOLEAN).description("포스트 공개 여부 (0: 비공개, 1: 공개)"),
                fieldWithPath("_embedded.posts[].series.id").type(NUMBER).description("시리즈 ID"),
                fieldWithPath("_embedded.posts[].series.name").type(STRING).description("시리즈 이름"),
                fieldWithPath("_embedded.posts[].thumbnail").type(STRING).description("썸네일 이미지 URL").optional(),
                fieldWithPath("_embedded.posts[].tags[].id").type(NUMBER).description("태그 ID"),
                fieldWithPath("_embedded.posts[].tags[].name").type(STRING).description("태그 이름")
        };
    }
}

