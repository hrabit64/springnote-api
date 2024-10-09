package com.springnote.api.tests.utils.image;

import com.springnote.api.config.ImageConfig;
import com.springnote.api.utils.image.ImageProcessor;
import com.springnote.api.utils.uuid.UuidUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;

@Slf4j
class ImageProcessorTest {

    @InjectMocks
    private ImageProcessor imageProcessor;

    @Mock
    private ImageConfig imageConfig;

    @Mock
    private UuidUtils uuidUtils;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    public static String encodeImageToBase64(String imagePath) throws IOException {
        // 클래스패스에서 이미지 파일을 불러오기
        var imgFile = new ClassPathResource(imagePath);

        // InputStream으로 이미지 읽기
        try (var inputStream = imgFile.getInputStream();
             var byteArrayOutputStream = new ByteArrayOutputStream()) {

            // InputStream에서 바이트 읽어서 ByteArrayOutputStream에 쓰기
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            // 바이트 배열로 변환
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Base64로 인코딩
            return Base64.getEncoder().encodeToString(imageBytes);
        }
    }

    @DisplayName("convertAndSaveImage")
    @Nested
    class ConvertAndSaveImage {
        @DisplayName("세로 이미지 (1559x1920)가 주어지면 (876x1080) 으로 변경하고 webp로 변환하여 저장한다.")
        @Test
        void convertAndSaveImage_SuccessVertical(
                @TempDir Path tempDir
        ) throws IOException {
            // given
            // test/resources 에서 테스트 이미지 파일을 읽어와서 base64로 인코딩
            var targetImage = "1559x1920.jpg";

            var base64Image = encodeImageToBase64("image/" + targetImage);

            var newUuid = UUID.randomUUID().toString();
            doReturn(newUuid).when(uuidUtils).generateUuid();
            doReturn(tempDir).when(imageConfig).getBasePath();
            doReturn(List.of("webp", "jpg", "jpeg", "png", "gif")).when(imageConfig).getAllowedFormat();


            // when
            var result = imageProcessor.convertAndSaveImage(base64Image, 1080, "webp");


            // then
            assertEquals("webp", result.targetFormat(), "결과값의 포맷이 'webp'이어야 합니다.");
            assertEquals(1080, result.height(), "결과값의 세로 크기가 1080이어야 합니다.");
            assertEquals(876, result.width(), "결과값의 가로 크기가 876이어야 합니다.");
            assertEquals(newUuid + ".webp", result.name(), "결과값의 파일명이 " + newUuid + ".webp 형식이어야 합니다.");

            // 1. 이미지 파일이 생성되었는지 확인
            var outputFilePath = tempDir.resolve(newUuid + ".webp").toString();
            var outputFile = new File(outputFilePath);
            assertTrue(outputFile.exists(), "이미지 파일이 생성되어야 합니다.");


            // 2. 이미지 크기 검증
            var bufferedImage = ImageIO.read(outputFile);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            assertEquals(1080, height, "이미지의 세로 크기가 1080이어야 합니다.");
            assertEquals(876, width, "이미지의 가로 크기가 876이어야 합니다.");

            // 3. 이미지 타입 검증
            assertTrue(outputFilePath.endsWith(".webp"), "이미지가 'webp' 형식으로 저장되어야 합니다.");
        }

        @DisplayName("가로 이미지 (1920x1323)가 주어지면 (1080x744) 으로 변경하고 webp로 변환하여 저장한다.")
        @Test
        void convertAndSaveImage_SuccessHorizontal(
                @TempDir Path tempDir
        ) throws IOException {
            // given
            // test/resources 에서 테스트 이미지 파일을 읽어와서 base64로 인코딩
            var targetImage = "1920x1323.jpg";

            var base64Image = encodeImageToBase64("image/" + targetImage);

            var newUuid = UUID.randomUUID().toString();
            doReturn(newUuid).when(uuidUtils).generateUuid();
            doReturn(tempDir).when(imageConfig).getBasePath();
            doReturn(List.of("webp", "jpg", "jpeg", "png", "gif")).when(imageConfig).getAllowedFormat();

            // when
            var result = imageProcessor.convertAndSaveImage(base64Image, 1080, "webp");

            // then
            assertEquals("webp", result.targetFormat(), "결과값의 포맷이 'webp'이어야 합니다.");
            assertEquals(1080, result.width(), "결과값의 가로 크기가 1080이어야 합니다.");
            assertEquals(744, result.height(), "결과값의 세로 크기가 744이어야 합니다.");
            assertEquals(newUuid + ".webp", result.name(), "결과값의 파일명이 " + newUuid + ".webp 형식이어야 합니다.");

            // 1. 이미지 파일이 생성되었는지 확인
            var outputFilePath = tempDir.resolve(newUuid + ".webp").toString();
            var outputFile = new File(outputFilePath);
            assertTrue(outputFile.exists(), "이미지 파일이 생성되어야 합니다.");

            // 2. 이미지 크기 검증
            var bufferedImage = ImageIO.read(outputFile);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            assertEquals(1080, width, "이미지의 가로 크기가 1080이어야 합니다.");
            assertEquals(744, height, "이미지의 세로 크기가 744이어야 합니다.");

            assertTrue(outputFilePath.endsWith(".webp"), "이미지가 'webp' 형식으로 저장되어야 합니다.");
        }

        @DisplayName("정사각형의 이미지 (1022x1022)가 주어지면 (1080x1080) 으로 변경하고 webp로 변환하여 저장한다.")
        @Test
        void convertAndSaveImage_SuccessSquare(
                @TempDir Path tempDir
        ) throws IOException {
            // given
            // test/resources 에서 테스트 이미지 파일을 읽어와서 base64로 인코딩
            var targetImage = "1022x1022.jpg";

            var base64Image = encodeImageToBase64("image/" + targetImage);

            var newUuid = UUID.randomUUID().toString();
            doReturn(newUuid).when(uuidUtils).generateUuid();
            doReturn(tempDir).when(imageConfig).getBasePath();
            doReturn(List.of("webp", "jpg", "jpeg", "png", "gif")).when(imageConfig).getAllowedFormat();

            // when
            var result = imageProcessor.convertAndSaveImage(base64Image, 1080, "webp");

            // then
            assertEquals("webp", result.targetFormat(), "결과값의 포맷�� 'webp'이어야 합니다.");
            assertEquals(1080, result.width(), "결과값의 가로 크기가 1080이어야 합니다.");
            assertEquals(1080, result.height(), "결과값의 세로 크기가 1080이어야 합니다.");
            assertEquals(newUuid + ".webp", result.name(), "결과값의 파일명이 " + newUuid + ".webp 형식이어야 합니다.");

            // 1. 이미지 파일이 생성되었는지 확인
            var outputFilePath = tempDir.resolve(newUuid + ".webp").toString();
            var outputFile = new File(outputFilePath);
            assertTrue(outputFile.exists(), "이미지 파일이 생성되어야 합니다.");

            // 2. 이미지 크기 검증
            var bufferedImage = ImageIO.read(outputFile);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            assertEquals(1080, width, "이미지의 가로 크기가 1080이어야 합니다.");
            assertEquals(1080, height, "이미지의 세로 크기가 1080이어야 합니다.");

            assertTrue(outputFilePath.endsWith(".webp"), "이미지가 'webp' 형식으로 저장되어야 합니다.");
        }

        @DisplayName("gif 1080x1080 이미지가 주어지면 gif (1000x1000) 으로 변환하여 저장한다.")
        @Test
        void convertAndSaveImage_SuccessGif(
                @TempDir Path tempDir
        ) throws IOException {
            // given
            // test/resources 에서 테스트 이미지 파일을 읽어와서 base64로 인코딩
            var targetImage = "1080x1080.gif";

            var base64Image = encodeImageToBase64("image/" + targetImage);

            var newUuid = UUID.randomUUID().toString();
            doReturn(newUuid).when(uuidUtils).generateUuid();
            doReturn(tempDir).when(imageConfig).getBasePath();
            doReturn(List.of("webp", "jpg", "jpeg", "png", "gif")).when(imageConfig).getAllowedFormat();

            // when
            var result = imageProcessor.convertAndSaveImage(base64Image, 1000, "gif");

            // then
            assertEquals("gif", result.targetFormat(), "결과값의 포맷이 'gif'이어야 합니다.");
            assertEquals(1000, result.width(), "결과값의 가로 크기가 1000이어야 합니다.");
            assertEquals(1000, result.height(), "결과값의 세로 크기가 1000이어야 합니다.");
            assertEquals(newUuid + ".gif", result.name(), "결과값의 파일명이 " + newUuid + ".gif 형식이어야 합니다.");

            // 1. 이미지 파일이 생성되었는지 확인
            var outputFilePath = tempDir.resolve(newUuid + ".gif").toString();
            var outputFile = new File(outputFilePath);
            assertTrue(outputFile.exists(), "이미지 파일이 생성되어야 합니다.");

            // 2. 이미지 크기 검증
            var bufferedImage = ImageIO.read(outputFile);
            int width = bufferedImage.getWidth();
            int height = bufferedImage.getHeight();

            assertEquals(1000, width, "이미지의 가로 크기가 1000이어야 합니다.");
            assertEquals(1000, height, "이미지의 세로 크기가 1000이어야 합니다.");

            assertTrue(outputFilePath.endsWith(".gif"), "이미지가 'gif' 형식으로 저장되어야 합니다.");
        }
    }
}