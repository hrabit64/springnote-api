package com.springnote.api.testUtils.docs;

import org.springframework.restdocs.payload.FieldDescriptor;

import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class GeneralFieldGenerator {
    public static FieldDescriptor[] pageFields() {
        return new FieldDescriptor[]{
                fieldWithPath("page").type(NUMBER).description("현재 페이지 번호"),
                fieldWithPath("size").type(NUMBER).description("페이지 크기"),
                fieldWithPath("total_elements").type(NUMBER).description("전체 요소 수"),
                fieldWithPath("total_pages").type(NUMBER).description("전체 페이지 수"),
        };
    }
}

