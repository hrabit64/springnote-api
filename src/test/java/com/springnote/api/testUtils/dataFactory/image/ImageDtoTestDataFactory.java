package com.springnote.api.testUtils.dataFactory.image;

import com.springnote.api.dto.image.common.ImageResponseCommonDto;
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Random;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class ImageDtoTestDataFactory {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static String generateBase64(int desiredLength) {
        // Base64 인코딩 후 길이를 맞추기 위한 계산
        int byteLength = (int) Math.ceil(desiredLength * 3 / 4.0);

        // 임의의 바이트 배열 생성
        byte[] randomBytes = new byte[byteLength];
        var random = new Random();
        random.nextBytes(randomBytes);

        // 바이트 배열을 Base64로 인코딩
        var base64String = Base64.getEncoder().encodeToString(randomBytes);

        // 최종 문자열이 원하는 길이가 되도록 자르기
        return base64String.substring(0, Math.min(desiredLength, base64String.length()));
    }


    public static ImageResponseCommonDto createImageResponseCommonDto() {
        return ImageResponseCommonDto.builder()
                .id(1L)
                .createdAt(testLocalDateTime())
                .width(100)
                .height(100)
                .format("webp")
                .convertedName("convertedName.jpg")
                .postId(1L)
                .build();
    }

    public static void createMatcher(ImageResponseCommonDto expected, ResultActions result, boolean isPageable) throws Exception {
        if (isPageable) {
            result.andExpect(jsonPath("$.content[0].id").value(expected.getId()))
                    .andExpect(jsonPath("$.content[0].created_at").value(expected.getCreatedAt().format(formatter)))
                    .andExpect(jsonPath("$.content[0].width").value(expected.getWidth()))
                    .andExpect(jsonPath("$.content[0].height").value(expected.getHeight()))
                    .andExpect(jsonPath("$.content[0].format").value(expected.getFormat()))
                    .andExpect(jsonPath("$.content[0].converted_name").value(expected.getConvertedName()))
                    .andExpect(jsonPath("$.content[0].post_id").value(expected.getPostId()));
        } else {
            result.andExpect(jsonPath("$.id").value(expected.getId()))
                    .andExpect(jsonPath("$.created_at").value(expected.getCreatedAt().format(formatter)))
                    .andExpect(jsonPath("$.width").value(expected.getWidth()))
                    .andExpect(jsonPath("$.height").value(expected.getHeight()))
                    .andExpect(jsonPath("$.format").value(expected.getFormat()))
                    .andExpect(jsonPath("$.converted_name").value(expected.getConvertedName()))
                    .andExpect(jsonPath("$.post_id").value(expected.getPostId()));
        }
    }
}
