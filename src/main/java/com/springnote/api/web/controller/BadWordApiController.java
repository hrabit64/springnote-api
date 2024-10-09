package com.springnote.api.web.controller;

import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.domain.badWord.BadWordSortKeys;
import com.springnote.api.dto.assembler.badWord.BadWordResponseCommonDtoAssembler;
import com.springnote.api.dto.badWord.common.BadWordResponseCommonDto;
import com.springnote.api.dto.badWord.controller.BadWordCreateRequestControllerDto;
import com.springnote.api.dto.general.common.MessageResponseCommonDto;
import com.springnote.api.service.BadWordService;
import com.springnote.api.utils.badWord.BadWordFilter;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.validation.pageable.size.PageableSizeCheck;
import com.springnote.api.utils.validation.pageable.sort.PageableSortKeyCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequestMapping("/api/v1/bad-word")
@RequiredArgsConstructor
@RestController
public class BadWordApiController {

    private final BadWordService badWordService;
    private final BadWordResponseCommonDtoAssembler assembler;
    private final PagedResourcesAssembler<BadWordResponseCommonDto> pagedResourcesAssembler;
    private final BadWordFilter badWordFilter;

    @EnableAuthentication(AuthLevel.ADMIN)
    @GetMapping
    public PagedModel<EntityModel<BadWordResponseCommonDto>> getBadWords(
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
            @PageableSortKeyCheck(sortKey = BadWordSortKeys.class)
            @PageableSizeCheck(max = 100)
            Pageable pageable,

            @RequestParam(value = "type", required = false)
            Boolean type,

            @RequestParam(value = "word", required = false)
            @Size(min = 2, max = 10, message = "검색어는 2자 이상, 10자 이하여야 합니다.")
            String word
    ) {
        Page<BadWordResponseCommonDto> data;
        if (word != null && !word.isEmpty()) {
            data = badWordService.getAllByWord(word, type, pageable);
        } else {
            data = badWordService.getAll(pageable, type);
        }
        return pagedResourcesAssembler.toModel(data, assembler);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PostMapping("/refresh")
    public ResponseEntity<MessageResponseCommonDto> refreshBadWords() {
        badWordFilter.refresh();
        return ResponseEntity.ok(new MessageResponseCommonDto("금칙어 목록을 갱신했습니다."));
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @GetMapping("/{id}")
    public EntityModel<BadWordResponseCommonDto> getBadWordById(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "금칙어 ID가 올바르지 않습니다.")
            @Min(value = 1, message = "금칙어 ID가 올바르지 않습니다.")
            Long id
    ) {
        var data = badWordService.getById(id);
        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PostMapping
    public EntityModel<BadWordResponseCommonDto> createBadWord(
            @RequestBody
            @Valid
            BadWordCreateRequestControllerDto requestDto
    ) {
        var data = badWordService.create(requestDto.toServiceDto());
        return assembler.toModel(data);
    }


    @EnableAuthentication(AuthLevel.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBadWord(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "금칙어 ID가 올바르지 않습니다.")
            @Min(value = 1, message = "금칙어 ID가 올바르지 않습니다.")
            Long id
    ) {
        badWordService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
