package com.springnote.api.web.controller;

import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.domain.QueryKeyUtil;
import com.springnote.api.domain.post.PostKeywordSearchMode;
import com.springnote.api.domain.post.PostQueryKeys;
import com.springnote.api.domain.post.PostSortKeys;
import com.springnote.api.dto.assembler.post.PostDetailResponseCommonDtoAssembler;
import com.springnote.api.dto.assembler.post.PostSimpleResponseCommonDtoAssembler;
import com.springnote.api.dto.general.controller.ValidationResultResponseDto;
import com.springnote.api.dto.post.common.PostDetailResponseCommonDto;
import com.springnote.api.dto.post.common.PostSimpleResponseCommonDto;
import com.springnote.api.dto.post.controller.PostCreateRequestControllerDto;
import com.springnote.api.dto.post.controller.PostStatusUpdateRequestControllerDto;
import com.springnote.api.dto.post.controller.PostUpdateRequestControllerDto;
import com.springnote.api.service.PostService;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.exception.validation.ValidationErrorCode;
import com.springnote.api.utils.exception.validation.ValidationException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.type.TypeParser;
import com.springnote.api.utils.validation.pageable.size.PageableSizeCheck;
import com.springnote.api.utils.validation.pageable.sort.PageableSortKeyCheck;
import com.springnote.api.utils.validation.query.QueryParamCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.springnote.api.utils.list.ListHelper.ignoreCaseContains;


@Slf4j
@Validated
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@RestController
public class PostApiController {

    private final PostService postService;
    private final PostDetailResponseCommonDtoAssembler postDetailAssembler;
    private final PostSimpleResponseCommonDtoAssembler postSimpleAssembler;
    private final PagedResourcesAssembler<PostSimpleResponseCommonDto> pagedResourcesAssembler;
    private final UserContext userContext;
    private final TypeParser typeParser;

    private final List<String> ignoreKeys = List.of("page", "size", "sort");

    @EnableAuthentication(AuthLevel.NONE)
    @GetMapping("/{id}")
    public EntityModel<PostDetailResponseCommonDto> getPostById(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            Long id
    ) {

        var result = postService.getById(id);
        validatePermission(result.isEnabled());

        return postDetailAssembler.toModel(result);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @GetMapping("/title/{title}")
    public ResponseEntity<ValidationResultResponseDto> getTitleIsExist(
            @Size(min = 3, max = 300, message = "제목은 3자 이상, 300자 이하여야 합니다.")
            @NotEmpty(message = "제목을 입력해주세요.")
            @PathVariable("title")
            String title
    ) {
        var result = postService.isExistTitle(title);
        return ResponseEntity.ok(new ValidationResultResponseDto(result));
    }

    @EnableAuthentication(AuthLevel.NONE)
    @GetMapping("")
    public PagedModel<EntityModel<PostSimpleResponseCommonDto>> getPosts(
            @RequestParam
            @QueryParamCheck(queryKey = PostQueryKeys.class)
            MultiValueMap<String, String> queryOptions,

            @PageableDefault(page = 0, size = 20, sort = "createdDate", direction = Sort.Direction.DESC)
            @PageableSizeCheck(max = 100)
            @PageableSortKeyCheck(sortKey = PostSortKeys.class)
            Pageable pageable
    ) {
        var typedQueryOptions = convertToTypedQueryParam(queryOptions);
        injectDefaultQueryOptions(typedQueryOptions);
        validateQueryOptionPermission(typedQueryOptions);

        var searchMode = getSearchMode(typedQueryOptions);
        var keyword = (searchMode == PostKeywordSearchMode.NONE) ? null : getKeyword(typedQueryOptions);

        Page<PostSimpleResponseCommonDto> result;


        switch (searchMode) {
            case NONE -> result = postService.getAllByQueryParams(typedQueryOptions, pageable);
            case TITLE -> result = postService.getAllByTitleKeyword(keyword, typedQueryOptions, pageable);
            case CONTENT -> result = postService.getAllByContentKeyword(keyword, typedQueryOptions, pageable);
            case MIX -> result = postService.getAllByMixKeyword(keyword, typedQueryOptions, pageable);

            // 이미 검증되었으므로 이 경우는 서버 오류
            default -> throw new IllegalArgumentException("잘못된 검색 모드가 주어졌습니다.");
        }

        return pagedResourcesAssembler.toModel(result, postSimpleAssembler);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostById(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            Long id
    ) {

        postService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PostMapping("")
    public EntityModel<PostDetailResponseCommonDto> createPost(
            @RequestBody
            @Valid
            PostCreateRequestControllerDto requestDto
    ) {

        var result = postService.create(requestDto.toServiceDto());

        return postDetailAssembler.toModel(result);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PutMapping("/{id}")
    public EntityModel<PostDetailResponseCommonDto> updatePost(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            Long id,

            @RequestBody
            @Valid
            PostUpdateRequestControllerDto requestDto
    ) {

        var result = postService.update(requestDto.toServiceDto(id));

        return postDetailAssembler.toModel(result);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @PutMapping("/{id}/status")
    public EntityModel<PostDetailResponseCommonDto> updatePostStatus(
            @PathVariable("id")
            @Max(value = DBTypeSize.INT, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            Long id,

            @RequestBody
            @Valid
            PostStatusUpdateRequestControllerDto requestDto
    ) {

        var result = postService.updateStatus(requestDto.toServiceDto(id));

        return postDetailAssembler.toModel(result);
    }

    /**
     * 쿼리 파라미터에서 검색 키워드를 가져옵니다. 이때 값이 없으면 예외를 발생시킵니다.
     * 값이 있으면 해당 값을 반환하고 쿼리 옵션에서 해당 키워드를 제거합니다.
     *
     * @param queryOptions 검색 옵션
     * @return 검색 모드에 따른 키워드
     */
    private String getKeyword(MultiValueMap<PostQueryKeys, String> queryOptions) {
        var keywordValues = queryOptions.get(PostQueryKeys.KEYWORD);

        // queryKey 검증을 통과했으므로 keywordValues는 null이 아님
        if (keywordValues == null || keywordValues.isEmpty()) {
            throw new IllegalArgumentException("keywordValues is null");
        }
        var keywordValue = keywordValues.get(0);

        queryOptions.remove(PostQueryKeys.KEYWORD);
        return keywordValue;
    }

    private void validatePermission(Boolean isOnlyOpenPost) {
        if (isOnlyOpenPost != null && !isOnlyOpenPost && !userContext.isAdmin()) {
            throw new ValidationException(ExceptionMessageFormatter.createBadPermissionMessage(), ValidationErrorCode.BAD_PERMISSION);
        }
    }

    /**
     * 검색 모드를 가져옵니다. 검색 모드가 없으면 NONE을 반환합니다.
     * 검색 모드가 있으면 해당 값을 반환하고 쿼리 옵션에서 해당 키워드를 제거합니다.
     *
     * @param queryOptions 쿼리 옵션
     * @return 검색 모드
     */
    private PostKeywordSearchMode getSearchMode(MultiValueMap<PostQueryKeys, String> queryOptions) {
        var option = queryOptions.get(PostQueryKeys.SEARCH_MODE);

        if (option == null) {
            return PostKeywordSearchMode.NONE;
        }

        queryOptions.remove(PostQueryKeys.SEARCH_MODE);

        return switch (option.get(0).toLowerCase()) {
            case "title" -> PostKeywordSearchMode.TITLE;
            case "content" -> PostKeywordSearchMode.CONTENT;
            case "mix" -> PostKeywordSearchMode.MIX;
            default -> throw new IllegalArgumentException("잘못된 검색 모드가 주어졌습니다."); // 이미 검증되었으므로 이 경우는 서버 오류
        };

    }

    /**
     * 기본 쿼리 옵션을 주입합니다.
     * <p>
     * 관리자가 아닌 경우 기본적으로 열린 포스트만 조회하도록 하며,
     * 관리자인 경우 기본적으로 모든 포스트를 조회하도록 합니다.
     *
     * @param queryOptions 쿼리 옵션
     */
    private void injectDefaultQueryOptions(MultiValueMap<PostQueryKeys, String> queryOptions) {
        if (!queryOptions.containsKey(PostQueryKeys.IS_ONLY_OPEN_POST) && !userContext.isAdmin()) {
            // 관리자가 아닌 경우 기본적으로 열린 포스트만 조회
            queryOptions.add(PostQueryKeys.IS_ONLY_OPEN_POST, "true");

        } else if (!queryOptions.containsKey(PostQueryKeys.IS_ONLY_OPEN_POST) && userContext.isAdmin()) {
            // 관리자인 경우 기본적으로 모든 포스트 조회
            queryOptions.add(PostQueryKeys.IS_ONLY_OPEN_POST, "false");
        }
    }

    /**
     * 쿼리 옵션의 권한을 검증합니다.
     * <p>
     * 관리자가 아닌데, isOnlyOpenPost가 false인 경우 권한이 없다는 예외를 발생시킵니다.
     *
     * @param queryOptions 쿼리 옵션
     */
    private void validateQueryOptionPermission(MultiValueMap<PostQueryKeys, String> queryOptions) {
        var isOnlyOpenPostValues = queryOptions.get(PostQueryKeys.IS_ONLY_OPEN_POST);

        // 이미 앞서 검증되었으므로 이 경우는 서버 오류
        if (isOnlyOpenPostValues == null || isOnlyOpenPostValues.isEmpty()) {
            throw new IllegalArgumentException("isOnlyOpenPostValues is null");
        }

        var isOnlyOpenPostValue = typeParser.parseBoolean(isOnlyOpenPostValues.get(0))
                // 이미 타입 검증을 통과했으므로 이 경우는 서버 오류
                .orElseThrow(() -> new IllegalArgumentException("isOnlyOpenPostValue's type is not boolean"));

        if (!isOnlyOpenPostValue && !userContext.isAdmin()) {
            throw new ValidationException(ExceptionMessageFormatter.createBadPermissionMessage(), ValidationErrorCode.BAD_PERMISSION);
        }
    }

    private MultiValueMap<PostQueryKeys, String> convertToTypedQueryParam(MultiValueMap<String, String> queryOptions) {
        var typedQueryOptions = new LinkedMultiValueMap<PostQueryKeys, String>();

        if (queryOptions == null || queryOptions.isEmpty()) {
            return typedQueryOptions;
        }

        queryOptions.forEach((key, values) -> {

            if (ignoreCaseContains(ignoreKeys, key)) {
                return;
            }

            var queryKey = QueryKeyUtil.getKey(PostQueryKeys.class, key);
            if (queryKey == null) {
                // 이미 검증되었으므로 이 경우는 서버 오류
                throw new IllegalArgumentException("queryKey is null");
            }
            typedQueryOptions.addAll(queryKey, values);
        });
        return typedQueryOptions;
    }

}
