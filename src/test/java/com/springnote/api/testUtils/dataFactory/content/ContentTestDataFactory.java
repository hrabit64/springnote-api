package com.springnote.api.testUtils.dataFactory.content;

import com.springnote.api.domain.content.Content;

public class ContentTestDataFactory {
    public static Content createContent() {
        return Content.builder()
                .id(1L)
                .editorText("editorText")
                .plainText("plainText")
                .build();
    }

    public static Content createContent(String editorText, String plainText) {
        return Content.builder()
                .id(1L)
                .editorText(editorText)
                .plainText(plainText)
                .build();
    }

    public static Content copyContent(Content content) {
        return Content.builder()
                .id(content.getId())
                .editorText(content.getEditorText())
                .plainText(content.getPlainText())
                .build();
    }
}
