package com.springnote.api.dto.content.controller;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.content.Content;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ContentResponseControllerDto {
    private String plainText;
    private String editorText;

    public ContentResponseControllerDto(Content content) {
        this.plainText = content.getPlainText();
        this.editorText = content.getEditorText();
    }
}
