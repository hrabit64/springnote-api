package com.springnote.api.dto.image.controller;

import com.springnote.api.dto.image.service.ImageCreateRequestServiceDto;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.validation.base64.Base64Check;
import com.springnote.api.utils.validation.number.NumberRangeCheck;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.*;

@EqualsAndHashCode
@ToString
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageCreateRequestControllerDto {

    // 파일 사이즈 제한은 5MB 이지만, BASE64 인코딩으로 인해 33% 증가된 용량을 고려하여 5MB * 1.33 = 5592405 (5.33MB)
    @Base64Check
    @Size(max = 5592405, message = "이미지는 5MB까지 업로드가 가능합니다.")
    @NotEmpty(message = "이미지를 선택해주세요.")
    private String image;

    @NumberRangeCheck(min = 1, max = DBTypeSize.INT, message = "포스트 ID 의 범위가 잘못되었습니다.", nullable = true)
    private Long postId;

    public ImageCreateRequestServiceDto toServiceDto() {
        return ImageCreateRequestServiceDto.builder()
                .image(image)
                .postId(postId)
                .build();
    }
}
