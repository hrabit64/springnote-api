package com.springnote.api.dto.image.service;

import lombok.*;

@EqualsAndHashCode
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageCreateRequestServiceDto {

    private String image;
    private Long postId;

}
