package com.springnote.api.web.controller;


import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.dto.assembler.config.ConfigResponseCommonDtoAssembler;
import com.springnote.api.dto.config.common.ConfigResponseCommonDto;
import com.springnote.api.service.ConfigService;
import com.springnote.api.utils.validation.string.CheckHasBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequestMapping("/api/v1/config")
@RequiredArgsConstructor
@RestController
public class ConfigApiController {

    private final ConfigService configService;
    private final ConfigResponseCommonDtoAssembler assembler;
    private final PagedResourcesAssembler<ConfigResponseCommonDto> pagedResourcesAssembler;

    @EnableAuthentication(AuthLevel.ADMIN)
    @GetMapping("/{key}")
    public EntityModel<ConfigResponseCommonDto> getConfigByKey(
            @PathVariable("key")
            @CheckHasBlank(message = "키에 공백이 포함되어 있습니다.")
            @NotEmpty(message = "key는 필수값입니다.")
            @Size(min = 1, max = 300, message = "key는 1자 이상 300자 이하로 입력해주세요.")
            String key
    ) {
        var val = configService.getConfig(key);
        var data = new ConfigResponseCommonDto(key, val);
        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PutMapping("/{key}")
    public EntityModel<ConfigResponseCommonDto> updateConfigByKey(
            @PathVariable("key")
            @CheckHasBlank(message = "키에 공백이 포함되어 있습니다.")
            @NotEmpty(message = "key는 필수값입니다.")
            @Size(min = 1, max = 300, message = "key는 1자 이상 300자 이하로 입력해주세요.")
            String key,

            @RequestParam(name = "value")
            @NotEmpty(message = "value는 필수값입니다.")
            @Size(min = 1, max = 300, message = "value는 1자 이상 300자 이하로 입력해주세요.")
            String value
    ) {
        var data = configService.updateConfig(key, value);
        return assembler.toModel(data);
    }


}
