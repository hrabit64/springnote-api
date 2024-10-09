package com.springnote.api.web.controller;

import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.dto.assembler.siteContent.SiteContentResponseCommonDtoAssembler;
import com.springnote.api.dto.siteContent.common.SiteContentResponseCommonDto;
import com.springnote.api.dto.siteContent.controller.SiteContentCreateRequestControllerDto;
import com.springnote.api.dto.siteContent.controller.SiteContentUpdateRequestControllerDto;
import com.springnote.api.service.SiteContentService;
import com.springnote.api.utils.validation.string.CheckHasBlank;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequestMapping("/api/v1/site-content")
@RequiredArgsConstructor
@RestController
public class SiteContentApiController {

    private final SiteContentService siteContentService;
    private final SiteContentResponseCommonDtoAssembler assembler;


    @GetMapping("/{key}")
    public EntityModel<SiteContentResponseCommonDto> getSiteContentByKey(
            @CheckHasBlank(message = "키에 공백이 포함되어 있습니다.")
            @Size(min = 1, max = 300, message = "키는 1자 이상, 300자 이하여야 합니다.")
            @NotEmpty(message = "제목을 입력해주세요.")
            @PathVariable("key")
            String key
    ) {
        var data = siteContentService.getSiteContentById(key);
        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PostMapping("")
    public EntityModel<SiteContentResponseCommonDto> createSiteContent(
            @RequestBody
            @Valid
            SiteContentCreateRequestControllerDto requestDto
    ) {
        var data = siteContentService.create(requestDto.toServiceDto());
        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PutMapping("/{key}")
    public EntityModel<SiteContentResponseCommonDto> updateSiteContent(
            @PathVariable("key")
            @CheckHasBlank(message = "키에 공백이 포함되어 있습니다.")
            @Size(min = 1, max = 300, message = "키는 1자 이상, 300자 이하여야 합니다.")
            @NotEmpty(message = "제목을 입력해주세요.")
            String key,

            @RequestBody
            @Valid
            SiteContentUpdateRequestControllerDto requestDto
    ) {
        var data = siteContentService.update(requestDto.toServiceDto(key));
        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteSiteContent(
            @PathVariable("key")
            @CheckHasBlank(message = "키에 공백이 포함되어 있습니다.")
            @Size(min = 1, max = 300, message = "키는 1자 이상, 300자 이하여야 합니다.")
            @NotEmpty(message = "제목을 입력해주세요.")
            String key
    ) {
        siteContentService.delete(key);

        return ResponseEntity.noContent().build();
    }


}
