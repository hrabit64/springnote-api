package com.springnote.api.web.controller;


import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.domain.tmpPost.TmpPostSortKeys;
import com.springnote.api.dto.assembler.post.PostDetailResponseCommonDtoAssembler;
import com.springnote.api.dto.assembler.tmpPost.TmpPostResponseCommonDtoAssembler;
import com.springnote.api.dto.post.common.PostDetailResponseCommonDto;
import com.springnote.api.dto.tmpPost.common.TmpPostResponseCommonDto;
import com.springnote.api.dto.tmpPost.controller.TmpPostCreateRequestControllerDto;
import com.springnote.api.dto.tmpPost.controller.TmpPostUpdateRequestControllerDto;
import com.springnote.api.service.TmpPostService;
import com.springnote.api.utils.validation.pageable.size.PageableSizeCheck;
import com.springnote.api.utils.validation.pageable.sort.PageableSortKeyCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.constraints.UUID;
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
@RequestMapping("/api/v1/tmp-post")
@RequiredArgsConstructor
@RestController
public class TmpPostApiController {

    private final TmpPostService tmpPostService;
    private final TmpPostResponseCommonDtoAssembler tmpPostAssembler;
    private final PagedResourcesAssembler<TmpPostResponseCommonDto> pagedResourcesAssembler;
    private final PostDetailResponseCommonDtoAssembler postDetailAssembler;

    @EnableAuthentication(AuthLevel.ADMIN)
    @PostMapping("")
    public EntityModel<TmpPostResponseCommonDto> createTmpPost(
            @RequestBody
            @Valid
            TmpPostCreateRequestControllerDto requestDto
    ) {
        var data = tmpPostService.create(requestDto.toServiceDto());
        return tmpPostAssembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @GetMapping("/{id}")
    public EntityModel<TmpPostResponseCommonDto> getTmpPostById(
            @PathVariable("id")
            @UUID(message = "잘못된 ID 형식입니다.")
            @NotEmpty(message = "ID는 필수입니다.")
            String id
    ) {
        var data = tmpPostService.getById(id);
        return tmpPostAssembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @GetMapping("")
    public PagedModel<EntityModel<TmpPostResponseCommonDto>> getTmpPostList(
            @PageableSortKeyCheck(sortKey = TmpPostSortKeys.class)
            @PageableSizeCheck(max = 50)
            @PageableDefault(page = 0, size = 20, sort = "createdDate", direction = Sort.Direction.DESC)
            Pageable pageable
    ) {
        var data = tmpPostService.getAll(pageable);
        return pagedResourcesAssembler.toModel(data, tmpPostAssembler);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PutMapping("/{id}")
    public EntityModel<TmpPostResponseCommonDto> updateTmpPost(
            @PathVariable("id")
            @UUID(message = "잘못된 ID 형식입니다.")
            @NotEmpty(message = "ID는 필수입니다.")
            String id,

            @RequestBody
            @Valid
            TmpPostUpdateRequestControllerDto requestDto
    ) {
        var data = tmpPostService.update(requestDto.toServiceDto(id));
        return tmpPostAssembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTmpPost(
            @PathVariable("id")
            @UUID(message = "잘못된 ID 형식입니다.")
            @NotEmpty(message = "ID는 필수입니다.")
            String id
    ) {
        tmpPostService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PostMapping("/{id}/publish")
    public EntityModel<PostDetailResponseCommonDto> publishTmpPost(
            @PathVariable("id")
            @UUID(message = "잘못된 ID 형식입니다.")
            @NotEmpty(message = "ID는 필수입니다.")
            String id
    ) {
        var data = tmpPostService.convertToPost(id);
        return postDetailAssembler.toModel(data);
    }
}
