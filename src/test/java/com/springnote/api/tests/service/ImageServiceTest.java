package com.springnote.api.tests.service;

import com.springnote.api.config.ImageConfig;
import com.springnote.api.domain.image.Image;
import com.springnote.api.domain.image.ImageRepository;
import com.springnote.api.dto.image.common.ImageResponseCommonDto;
import com.springnote.api.dto.image.service.ImageCreateRequestServiceDto;
import com.springnote.api.service.ImageService;
import com.springnote.api.testUtils.dataFactory.TestDataFactory;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.image.ImageProcessor;
import com.springnote.api.utils.image.ImageResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("Service Test - ImageService")
public class ImageServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageRepository imageRepository;

    @Mock
    private ImageProcessor imageProcessor;

    @Mock
    private ImageConfig imageConfig;

    @DisplayName("ImageService.delete")
    @Nested
    class delete {

        @DisplayName("올바른 이미지 ID가 주어지면 이미지를 삭제한다.")
        @Test
        void delete_successWithValidImageId() {
            // given
            var imageId = 1L;

            var image = Image.builder()
                    .id(imageId)
                    .convertedName("c5ba0314-787a-4df2-bb92-d444adf79b9d.webp")
                    .build();

            doReturn(Optional.of(image)).when(imageRepository).findById(imageId);

            // when
            imageService.deleteById(imageId);


            // then
            verify(imageRepository).delete(ArgumentMatchers.eq(image));
            verify(imageProcessor).deleteImage(ArgumentMatchers.eq(image.getConvertedName()));

        }

        @DisplayName("존재하지 않는 이미지 ID가 주어지면, 에러를 발생시킨다.")
        @Test
        void delete_failWithNotExistingImageId() {
            // given
            var imageId = 1L;

            doReturn(Optional.empty()).when(imageRepository).findById(imageId);

            // when
            var result = assertThrows(BusinessException.class, () -> imageService.deleteById(imageId));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(imageRepository).findById(imageId);
        }
    }

    @DisplayName("ImageService.deleteByName")
    @Nested
    class deleteByName {

        @DisplayName("올바른 이미지 이름이 주어지면 이미지를 삭제한다.")
        @Test
        void deleteByName_successWithValidConvertedName() {
            // given
            var convertedName = "c5ba0314-787a-4df2-bb92-d444adf79b9d.webp";

            var image = Image.builder()
                    .convertedName(convertedName)
                    .build();

            doReturn(Optional.of(image)).when(imageRepository).findByConvertedName(convertedName);

            // when
            imageService.deleteByName(convertedName);

            // then
            verify(imageRepository).delete(ArgumentMatchers.eq(image));
            verify(imageProcessor).deleteImage(ArgumentMatchers.eq(image.getConvertedName()));
        }

        @DisplayName("존재하지 않는 이미지 이름이 주어지면, 에러를 발생시킨다.")
        @Test
        void deleteByName_failWithNotExistingConvertedName() {
            // given
            var convertedName = "c5ba0314-787a-4df2-bb92-d444adf79b9d.webp";

            doReturn(Optional.empty()).when(imageRepository).findByConvertedName(convertedName);

            // when
            var result = assertThrows(BusinessException.class, () -> imageService.deleteByName(convertedName));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(imageRepository).findByConvertedName(convertedName);
        }
    }

    @DisplayName("ImageService.create")
    @Nested
    class create {

        @DisplayName("올바른 이미지가 주어지면, 이미지를 생성한다.")
        @Test
        void create_successWithValidImage() throws IOException {
            // given
            var requestDto = ImageCreateRequestServiceDto.builder()
                    .image("image")
                    .build();

            var imageConvertResult = new ImageResult("png", "png", 100, 100, "c5ba0314-787a-4df2-bb92-d444adf79b9d.webp");
            doReturn(imageConvertResult).when(imageProcessor).convertAndSaveImage(requestDto.getImage(), 100, "png");

            var image = Image.builder()
                    .convertedName(imageConvertResult.name())
                    .width(imageConvertResult.width())
                    .height(imageConvertResult.height())
                    .format(imageConvertResult.originalFormat())
                    .build();

            var savedImage = Image.builder()
                    .id(1L)
                    .convertedName(imageConvertResult.name())
                    .width(imageConvertResult.width())
                    .height(imageConvertResult.height())
                    .format(imageConvertResult.originalFormat())
                    .build();

            doReturn(savedImage).when(imageRepository).save(image);
            doReturn(100).when(imageConfig).getMaxWidth();
            doReturn("png").when(imageConfig).getBaseFormat();

            // when
            var result = imageService.create(requestDto);

            // then
            var exceptedImage = ImageResponseCommonDto.builder()
                    .id(savedImage.getId())
                    .convertedName(savedImage.getConvertedName())
                    .width(savedImage.getWidth())
                    .height(savedImage.getHeight())
                    .format(savedImage.getFormat())
                    .build();

            assertEquals(exceptedImage, result);
            verify(imageProcessor).convertAndSaveImage(ArgumentMatchers.eq(requestDto.getImage()), ArgumentMatchers.eq(100), ArgumentMatchers.eq("png"));
            verify(imageRepository).save(ArgumentMatchers.eq(image));
        }

        @DisplayName("이미지 변환중 오류가 발생하면, 에러를 발생시킨다.")
        @Test
        void create_failWithFailConvertImage() throws IOException {
            // given
            var requestDto = ImageCreateRequestServiceDto.builder()
                    .image("image")
                    .build();

            doThrow(new IOException()).when(imageProcessor).convertAndSaveImage(requestDto.getImage(), 100, "png");
            doReturn(100).when(imageConfig).getMaxWidth();
            doReturn("png").when(imageConfig).getBaseFormat();
            // when
            var result = assertThrows(BusinessException.class, () -> imageService.create(requestDto));

            // then
            assertEquals(BusinessErrorCode.NOT_VALID_ITEM, result.getErrorCode());
            verify(imageProcessor).convertAndSaveImage(ArgumentMatchers.eq(requestDto.getImage()), ArgumentMatchers.eq(100), ArgumentMatchers.eq("png"));
        }

        @DisplayName("허용되지 않은 이미지 포맷이 주어지면, 에러를 발생시킨다.")
        @Test
        void create_failWithNotAllowedFormat() throws IOException {
            // given
            var requestDto = ImageCreateRequestServiceDto.builder()
                    .image("image")
                    .build();

            doThrow(new IllegalStateException()).when(imageProcessor).convertAndSaveImage(requestDto.getImage(), 100, "png");
            doReturn(100).when(imageConfig).getMaxWidth();
            doReturn("png").when(imageConfig).getBaseFormat();
            // when
            var result = assertThrows(BusinessException.class, () -> imageService.create(requestDto));

            // then
            assertEquals(BusinessErrorCode.NOT_VALID_ITEM, result.getErrorCode());
            verify(imageProcessor).convertAndSaveImage(ArgumentMatchers.eq(requestDto.getImage()), ArgumentMatchers.eq(100), ArgumentMatchers.eq("png"));
        }

        @DisplayName("올바르지 않은 이미지가 주어지면, 에러를 발생시킨다.")
        @Test
        void create_failWithInvalidImage() throws IOException {
            // given
            var requestDto = ImageCreateRequestServiceDto.builder()
                    .image("image")
                    .build();

            doThrow(new IllegalArgumentException()).when(imageProcessor).convertAndSaveImage(ArgumentMatchers.eq(requestDto.getImage()), ArgumentMatchers.eq(100), ArgumentMatchers.eq("png"));
            doReturn(100).when(imageConfig).getMaxWidth();
            doReturn("png").when(imageConfig).getBaseFormat();
            // when
            var result = assertThrows(BusinessException.class, () -> imageService.create(requestDto));

            // then
            assertEquals(BusinessErrorCode.NOT_VALID_ITEM, result.getErrorCode());
            verify(imageProcessor).convertAndSaveImage(ArgumentMatchers.eq(requestDto.getImage()), ArgumentMatchers.eq(100), ArgumentMatchers.eq("png"));
        }
    }

    @DisplayName("ImageService.getAll")
    @Nested
    class getAll {

        @DisplayName("이미지가 존재하면, 모든 이미지를 반환한다.")
        @Test
        void getAll_successWithExistingImages() {
            // given

            var image = Image.builder()
                    .id(1L)
                    .convertedName("c5ba0314-787a-4df2-bb92-d444adf79b9d.webp")
                    .build();
            doReturn(TestDataFactory.createPageObject(image)).when(imageRepository).findAll(any(Pageable.class));

            // when
            var result = imageService.getAll(TestDataFactory.getMockPageable());

            // then
            var exceptedImage = ImageResponseCommonDto.builder()
                    .id(image.getId())
                    .convertedName(image.getConvertedName())
                    .build();
            assertEquals(1L, result.getTotalElements());
            assertEquals(exceptedImage, result.getContent().get(0));
            verify(imageRepository).findAll(any(Pageable.class));
        }
    }
}
