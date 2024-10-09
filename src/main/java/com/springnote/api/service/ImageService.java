package com.springnote.api.service;

import com.springnote.api.config.ImageConfig;
import com.springnote.api.domain.image.Image;
import com.springnote.api.domain.image.ImageRepository;
import com.springnote.api.dto.image.common.ImageResponseCommonDto;
import com.springnote.api.dto.image.service.ImageCreateRequestServiceDto;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import com.springnote.api.utils.image.ImageProcessor;
import com.springnote.api.utils.image.ImageResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;


@Slf4j
@RequiredArgsConstructor
@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final ImageProcessor imageProcessor;
    private final ImageConfig imageConfig;

    @Transactional
    public Page<ImageResponseCommonDto> getAll(Pageable pageable) {
        var data = imageRepository.findAll(pageable);
        return data.map(ImageResponseCommonDto::new);
    }

    @Transactional
    public void deleteById(Long imageId) {
        var image = fetchImageById(imageId);
        imageRepository.delete(image);
        if (!imageProcessor.deleteImage(image.getConvertedName())) {
            log.warn("이미지 정보를 삭제했지만 이미지 파일을 삭제하지 못했습니다. 이미지 이름: {}", image.getConvertedName());
        }
    }

    @Transactional
    public void deleteByName(String convertedName) {
        var image = imageRepository.findByConvertedName(convertedName).orElseThrow(
                () -> new BusinessException(ExceptionMessageFormatter.createItemNotFoundMessage(convertedName, "이미지"),
                        BusinessErrorCode.ITEM_NOT_FOUND)
        );
        imageRepository.delete(image);
        if (!imageProcessor.deleteImage(image.getConvertedName())) {
            log.warn("이미지 정보를 삭제했지만 이미지 파일을 삭제하지 못했습니다. 이미지 이름: {}", image.getConvertedName());
        }
    }

    @Transactional
    public ImageResponseCommonDto create(ImageCreateRequestServiceDto requestDto) throws IOException {
        ImageResult convertedImage;
        try {
            convertedImage = imageProcessor.convertAndSaveImage(requestDto.getImage(), imageConfig.getMaxWidth(), imageConfig.getBaseFormat());
        } catch (IOException e) {
            log.debug("이미지 변환 중 오류가 발생했습니다.", e);
            throw new BusinessException("이미지 변환 중 오류가 발생했습니다.", BusinessErrorCode.NOT_VALID_ITEM);
        } catch (IllegalArgumentException e) {
            log.debug("올바르지 않은 이미지 입니다.", e);
            throw new BusinessException("올바르지 않은 이미지 입니다.", BusinessErrorCode.NOT_VALID_ITEM);
        } catch (IllegalStateException e) {
            log.debug("허용되지 않은 이미지 포맷입니다.", e);
            throw new BusinessException("허용되지 않은 이미지 포맷입니다.", BusinessErrorCode.NOT_VALID_ITEM);
        }
        var image = Image.builder()
                .convertedName(convertedImage.name())
                .width(convertedImage.width())
                .height(convertedImage.height())
                .format(convertedImage.originalFormat())
                .build();
        var savedImage = imageRepository.save(image);
        return new ImageResponseCommonDto(savedImage);

    }

    private Image fetchImageById(Long imageId) {
        return imageRepository.findById(imageId).orElseThrow(
                () -> new BusinessException(ExceptionMessageFormatter.createItemNotFoundMessage(imageId.toString(), "이미지"),
                        BusinessErrorCode.ITEM_NOT_FOUND)
        );
    }


}
