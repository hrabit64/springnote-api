package com.springnote.api.web.controller;


import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.domain.image.ImageSortKeys;
import com.springnote.api.dto.general.common.MessageResponseCommonDto;
import com.springnote.api.dto.image.common.ImageResponseCommonDto;
import com.springnote.api.dto.image.controller.ImageCreateRequestControllerDto;
import com.springnote.api.service.ImageService;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.validation.pageable.size.PageableSizeCheck;
import com.springnote.api.utils.validation.pageable.sort.PageableSortKeyCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequestMapping("/api/v1/image")
@RequiredArgsConstructor
@RestController
public class ImageApiController {

    private final ImageService imageService;

    @EnableAuthentication(AuthLevel.ADMIN)
    @PostMapping
    public ResponseEntity<MessageResponseCommonDto> uploadImage(
            @RequestBody @Valid ImageCreateRequestControllerDto requestDto
    ) throws Exception {
        var result = imageService.create(requestDto.toServiceDto());
        return ResponseEntity.ok(new MessageResponseCommonDto(result.getConvertedName()));
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(
            @PathVariable("imageId")
            @Max(value = DBTypeSize.INT, message = "이미지 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "이미지 ID가 유효한 범위를 벗어났습니다.")
            Long imageId
    ) {
        imageService.deleteById(imageId);
        return ResponseEntity.noContent().build();
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @GetMapping("")
    public ResponseEntity<Page<ImageResponseCommonDto>> getImages(
            @PageableDefault(page = 0, size = 20, sort = "createdAt", direction = Sort.Direction.DESC)
            @PageableSizeCheck(max = 100)
            @PageableSortKeyCheck(sortKey = ImageSortKeys.class)
            Pageable pageable
    ) {
        var images = imageService.getAll(pageable);
        return ResponseEntity.ok(images);
    }
}
