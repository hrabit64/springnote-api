package com.springnote.api.dto.image.service;

import lombok.*;

@EqualsAndHashCode
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageConvertResultServiceDto {

    private String format;
    private byte[] convertedImage;
    private Long width;
    private Long height;

}
