package com.springnote.api.web.controller;

import com.springnote.api.domain.postType.PostTypeSortKey;
import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.service.PostTypeService;
import com.springnote.api.utils.validation.pageable.size.PageableSizeCheck;
import com.springnote.api.utils.validation.pageable.sort.PageableSortKeyCheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RequestMapping("/api/v1/post-type")
@RequiredArgsConstructor
@RestController
public class PostTypeApiController {

    private final PostTypeService postTypeService;
    private final PagedResourcesAssembler<PostTypeResponseDto> pagedResourcesAssembler;

    @GetMapping("")
    public PagedModel<EntityModel<PostTypeResponseDto>> getPostTypes(
            @PageableDefault(page = 0, size = 20, sort = "id", direction = Sort.Direction.DESC)
            @PageableSortKeyCheck(sortKey = PostTypeSortKey.class)
            @PageableSizeCheck(max = 20)
            Pageable pageable
    ) {
        var data = postTypeService.get(pageable);
        return pagedResourcesAssembler.toModel(data);
    }

}
