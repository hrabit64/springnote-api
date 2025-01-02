package com.springnote.api.web.controller;

import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.config.CommentConfig;
import com.springnote.api.domain.comment.CommentSortKeys;
import com.springnote.api.dto.assembler.comment.CommentResponseCommonDtoAssembler;
import com.springnote.api.dto.assembler.comment.CommentResponseWithReplyCntCommonDtoAssembler;
import com.springnote.api.dto.assembler.comment.ReplyResponseCommonDtoAssembler;
import com.springnote.api.dto.comment.common.CommentResponseCommonDto;
import com.springnote.api.dto.comment.common.CommentResponseWithReplyCntCommonDto;
import com.springnote.api.dto.comment.common.ReplyResponseCommonDto;
import com.springnote.api.dto.comment.controller.CommentCreateRequestControllerDto;
import com.springnote.api.dto.comment.controller.CommentUpdateRequestControllerDto;
import com.springnote.api.dto.comment.controller.ReplyCreateRequestControllerDto;
import com.springnote.api.security.captcha.CaptchaManager;
import com.springnote.api.service.CommentService;
import com.springnote.api.utils.context.RequestContext;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.exception.validation.ValidationErrorCode;
import com.springnote.api.utils.exception.validation.ValidationException;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.validation.bot.CheckCaptcha;
import com.springnote.api.utils.validation.pageable.size.PageableSizeCheck;
import com.springnote.api.utils.validation.pageable.sort.PageableSortKeyCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@RestController
public class CommentApiController {

    private final CommentService commentService;
    private final CommentResponseCommonDtoAssembler commentAssembler;
    private final ReplyResponseCommonDtoAssembler replyAssembler;
    private final CommentResponseWithReplyCntCommonDtoAssembler commentWithReplyCntAssembler;
    private final PagedResourcesAssembler<CommentResponseCommonDto> commentPagedResourcesAssembler;
    private final PagedResourcesAssembler<ReplyResponseCommonDto> replyPagedResourcesAssembler;
    private final PagedResourcesAssembler<CommentResponseWithReplyCntCommonDto> commentWithReplyCntPagedResourcesAssembler;
    private final UserContext userContext;
    private final RequestContext requestContext;
    private final CommentConfig commentConfig;
    private final CaptchaManager captchaManager;

    @EnableAuthentication(AuthLevel.NONE)
    @GetMapping("/post/{postId}/comment")
    public PagedModel<EntityModel<CommentResponseWithReplyCntCommonDto>> getCommentsWithPost(
            @PathVariable("postId")
            @Max(value = DBTypeSize.INT, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            Long postId,

            @PageableDefault(page = 0, size = 20, sort = "createdDate", direction = Sort.Direction.DESC)
            @PageableSortKeyCheck(sortKey = CommentSortKeys.class)
            @PageableSizeCheck(max = 50)
            Pageable pageable
    ) {
        checkCommentCanView();
        var data = commentService.getParentCommentWithPost(postId, pageable);
        return commentWithReplyCntPagedResourcesAssembler.toModel(data, commentWithReplyCntAssembler);
    }

    @EnableAuthentication(AuthLevel.NONE)
    @GetMapping("/comment/{commentId}/reply")
    public PagedModel<EntityModel<ReplyResponseCommonDto>> getReply(

            @PathVariable("commentId")
            @Max(value = DBTypeSize.INT, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            Long commentId,

            @PageableDefault(page = 0, size = 20, sort = "createdDate", direction = Sort.Direction.DESC)
            @PageableSortKeyCheck(sortKey = CommentSortKeys.class)
            @PageableSizeCheck(max = 50)
            Pageable pageable
    ) {
        checkCommentCanView();
        var data = commentService.getReply(commentId, pageable);
        return replyPagedResourcesAssembler.toModel(data, replyAssembler);
    }

    @EnableAuthentication(AuthLevel.USER)
    @PostMapping("/post/{postId}/comment")
    public EntityModel<CommentResponseCommonDto> createComment(
            @PathVariable("postId")
            @Max(value = DBTypeSize.INT, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            Long postId,

            @RequestBody @Valid CommentCreateRequestControllerDto requestDto
    ) {
        checkCaptcha(requestDto.getCaptchaToken());
        checkCommentCanWrite();

        var comment = commentService.create(requestDto.toServiceDto(postId, requestContext.getIp()));

        return commentAssembler.toModel(comment);
    }

    @EnableAuthentication(AuthLevel.USER)
    @PostMapping("/{postId}/comment/{commentId}/reply")
    public EntityModel<CommentResponseCommonDto> createReply(
            @PathVariable("postId")
            @Max(value = DBTypeSize.INT, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "포스트 ID가 유효한 범위를 벗어났습니다.")
            Long postId,

            @PathVariable("commentId")
            @Max(value = DBTypeSize.INT, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            Long commentId,

            @RequestBody @Valid ReplyCreateRequestControllerDto requestDto
    ) {
        checkCaptcha(requestDto.getCaptchaToken());
        checkCommentCanWrite();


        var comment = commentService.createReply(requestDto.toServiceDto(postId, commentId, requestContext.getIp()));
        return commentAssembler.toModel(comment);
    }

    @EnableAuthentication(AuthLevel.USER)
    @PutMapping("/comment/{commentId}")
    public EntityModel<CommentResponseCommonDto> updateComment(

            @PathVariable("commentId")
            @Max(value = DBTypeSize.INT, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            Long commentId,

            @RequestBody @Valid CommentUpdateRequestControllerDto requestDto
    ) {
        checkCaptcha(requestDto.getCaptchaToken());
        checkCommentCanWrite();

        var data = commentService.update(requestDto.toServiceDto(commentId));
        return commentAssembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.USER)
    @PutMapping("/comment/{commentId}/status")
    public EntityModel<CommentResponseCommonDto> updateCommentStatus(

            @PathVariable("commentId")
            @Max(value = DBTypeSize.INT, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            Long commentId

    ) {
        checkCommentCanWrite();
        var data = commentService.disabled(commentId);
        return commentAssembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.NONE)
    @GetMapping("/comment/{commentId}")
    public EntityModel<CommentResponseCommonDto> getCommentById(
            @PathVariable("commentId")
            @Max(value = DBTypeSize.INT, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            Long commentId
    ) {
        checkCommentCanView();
        var data = commentService.getCommentById(commentId);
        return commentAssembler.toModel(data);
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable("commentId")
            @Max(value = DBTypeSize.INT, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            @Min(value = 1, message = "댓글 ID가 유효한 범위를 벗어났습니다.")
            Long commentId
    ) {
//        checkCommentCanWrite();
        commentService.delete(commentId);
        return ResponseEntity.noContent().build();
    }

    private void checkCommentCanView() {
        if (!(commentConfig.isViewEnable() || userContext.isAdmin())) {
            throw new ValidationException("현재 댓글 조회가 불가능합니다.", ValidationErrorCode.BAD_PERMISSION);
        }

    }

    private void checkCommentCanWrite() {
        if (!(commentConfig.isWriteEnable() || userContext.isAdmin())) {
            throw new ValidationException("현재 댓글 작성이 불가능합니다.", ValidationErrorCode.BAD_PERMISSION);
        }
    }

    private void checkCaptcha(String captchaToken) {
        if(!captchaManager.verify(captchaToken)) {
            throw new ValidationException("캡차 인증에 실패했습니다.", ValidationErrorCode.BAD_ARGS);
        }
    }

}
