package com.springnote.api.utils.image;

import com.springnote.api.config.ImageConfig;
import com.springnote.api.utils.uuid.UuidUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Base64;

@RequiredArgsConstructor
@Component
public class ImageProcessor {

    private final ImageConfig imageConfig;
    private final UuidUtils uuidUtils;

    public ImageResult convertAndSaveImage(String base64Image, int targetSize, String targetFormat) throws IOException {
        // Base64 디코딩
        var decodedBytes = Base64.getDecoder().decode(base64Image);

        // BufferedImage로 변환 및 원본 포맷 확인
        String originalFormat;
        try (var inputStream = new ByteArrayInputStream(decodedBytes)) {
            var originalImage = ImageIO.read(inputStream);

            if (originalImage == null) {
                throw new IOException("Unable to read image");
            }

            originalFormat = getOriginalFormat(decodedBytes);

            // 허용된 포맷인지 검증
            validateFormat(originalFormat);
            validateFormat(targetFormat);

            // 이미지 크기 변환
            var calculatedSize = calculateNewSize(originalImage.getWidth(), originalImage.getHeight(), targetSize);

            var resizedImage = resizeImage(originalImage, calculatedSize.width(), calculatedSize.height());
            var newFormat = (originalFormat.equalsIgnoreCase("gif")) ? "gif" : targetFormat;
            var newName = saveImageToFile(resizedImage, newFormat, imageConfig.getBasePath());

            // 결과 객체 생성 및 리턴
            return new ImageResult(originalFormat, targetFormat, calculatedSize.width, calculatedSize.height, newName);
        }
    }

    public boolean deleteImage(String imageName) {
        var file = new File(imageConfig.getBasePath().resolve(imageName).toString());
        return file.delete();
    }

    public String getOriginalFormat(byte[] imageData) throws IOException {
        // 원본 이미지의 포맷을 확인
        try (var inputStream = new ByteArrayInputStream(imageData);
             var imageInputStream = ImageIO.createImageInputStream(inputStream)) {

            var readers = ImageIO.getImageReaders(imageInputStream);
            if (readers.hasNext()) {
                var reader = readers.next();
                return reader.getFormatName();
            } else {
                throw new IOException("Unable to determine original image format");
            }
        } catch (Exception e) {
            throw new IOException("Unable to determine original image format");
        }
    }

    private void validateFormat(String format) {
        if (!imageConfig.getAllowedFormat().contains(format.toLowerCase())) {
            throw new IllegalStateException("Unsupported image format: " + format);
        }
    }

    private BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        var resizedImage = new BufferedImage(targetWidth, targetHeight, originalImage.getType() != 0 ? originalImage.getType() : BufferedImage.TYPE_INT_RGB);

        var g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.drawImage(originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH), 0, 0, null);
        g2d.dispose();

        return resizedImage;
    }


    private String saveImageToFile(BufferedImage image, String format, Path savePath) throws IOException {
        var name = uuidUtils.generateUuid() + "." + format;

        var file = new File(savePath.resolve(name).toString());
        try (var outputStream = new FileOutputStream(file)) {
            ImageIO.write(image, format, outputStream);
        }

        return name;
    }

    private CalculatedSize calculateNewSize(int originalWidth, int originalHeight, int targetSize) {

        //가로 이미지일 경우
        if (originalWidth > originalHeight) {
            var newHeight = calculateProportional(originalWidth, originalHeight, targetSize);
            return new CalculatedSize(targetSize, newHeight);
        } else {
            //세로 이미지거나 정사각형 이미지일 경우
            var newWidth = calculateProportional(originalHeight, originalWidth, targetSize);
            return new CalculatedSize(newWidth, targetSize);
        }
    }

    private int calculateProportional(int a, int b, int targetSize) {
        return (int) ((double) targetSize * b / a);
    }

    private record CalculatedSize(int width, int height) {
    }


}

