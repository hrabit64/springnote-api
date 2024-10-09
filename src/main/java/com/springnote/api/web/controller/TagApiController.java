package com.springnote.api.web.controller;

import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.domain.tag.TagSortKeys;
import com.springnote.api.dto.assembler.tag.TagResponseDtoAssembler;
import com.springnote.api.dto.tag.common.TagResponseDto;
import com.springnote.api.dto.tag.controller.TagCreateRequestControllerDto;
import com.springnote.api.dto.tag.controller.TagUpdateRequestControllerDto;
import com.springnote.api.service.TagService;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.validation.pageable.size.PageableSizeCheck;
import com.springnote.api.utils.validation.pageable.sort.PageableSortKeyCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RequestMapping("/api/v1/tag")
@RequiredArgsConstructor
@RestController
public class TagApiController {

    private final TagService tagService;
    private final TagResponseDtoAssembler assembler;
    private final PagedResourcesAssembler<TagResponseDto> pagedResourcesAssembler;

    @GetMapping("")
    public PagedModel<EntityModel<TagResponseDto>> getTags(


            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC)
            @PageableSortKeyCheck(sortKey = TagSortKeys.class)
            @PageableSizeCheck(max = 20)
            Pageable pageable,

            @Size(min = 2, max = 100, message = "태그 이름은 2자 이상, 100자 이하여야 합니다.")
            @RequestParam(required = false, name = "name") String name
    ) {

        var data = (name == null) ? tagService.getAll(pageable) : tagService.getByName(name, pageable);

        return pagedResourcesAssembler.toModel(data, assembler);
    }

    @GetMapping("/{id}")
    public EntityModel<TagResponseDto> getTagById(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "태그 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "태그 ID가 유효한 범위를 벗어났습니다.")
            Long id
    ) {
        var data = tagService.getById(id);
        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PostMapping("")
    public EntityModel<TagResponseDto> createTag(
            @RequestBody
            @Valid
            TagCreateRequestControllerDto requestDto
    ) {
        var data = tagService.create(requestDto.toServiceDto());
        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PutMapping("/{id}")
    public EntityModel<TagResponseDto> updateTag(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "태그 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "태그 ID가 유효한 범위를 벗어났습니다.")
            Long id,

            @RequestBody
            @Valid
            TagUpdateRequestControllerDto requestDto
    ) {
        var data = tagService.update(requestDto.toServiceDto(id));
        return assembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "태그 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "태그 ID가 유효한 범위를 벗어났습니다.")
            Long id
    ) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
