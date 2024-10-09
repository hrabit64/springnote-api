package com.springnote.api.dto.image.common;


import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.image.Image;
import lombok.*;

import java.time.LocalDateTime;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ImageResponseCommonDto {
    private Long id;
    private String convertedName;
    private int width;
    private int height;
    private LocalDateTime createdAt;
    private String format;
    private Long postId;

    public ImageResponseCommonDto(Image image) {
        this.id = image.getId();
        this.postId = image.getPostId();
        this.convertedName = image.getConvertedName();
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.createdAt = image.getCreatedAt();
        this.format = image.getFormat();

    }
}
