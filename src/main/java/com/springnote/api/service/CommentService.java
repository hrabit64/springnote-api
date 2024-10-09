package com.springnote.api.service;

import com.springnote.api.domain.comment.Comment;
import com.springnote.api.domain.comment.CommentRepository;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.post.PostRepository;
import com.springnote.api.domain.user.User;
import com.springnote.api.domain.user.UserRepository;
import com.springnote.api.dto.comment.common.CommentResponseCommonDto;
import com.springnote.api.dto.comment.common.CommentResponseWithReplyCntCommonDto;
import com.springnote.api.dto.comment.common.ReplyResponseCommonDto;
import com.springnote.api.dto.comment.service.CommentCreateRequestServiceDto;
import com.springnote.api.dto.comment.service.CommentReplyCountResponseDto;
import com.springnote.api.dto.comment.service.CommentUpdateRequestServiceDto;
import com.springnote.api.dto.comment.service.ReplyCreateRequestServiceDto;
import com.springnote.api.utils.badWord.BadWordFilter;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import com.springnote.api.utils.formatter.ExceptionMessageFormatter;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.HtmlUtils;

import java.util.Objects;

@RequiredArgsConstructor
@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final BadWordFilter badWordFilter;
    private final UserContext userContext;
    private final UserRepository userRepository;

    @Transactional
    public CommentResponseCommonDto create(CommentCreateRequestServiceDto requestDto) {

        var targetPost = fetchPostAndValidate(requestDto.getPostId());

        var filteredContent = filteringCommentContent(requestDto.getContent());

        //이미 인증파트에서 유저 활성화 여부를 확인하여 비활성화된 유저는 로그인을 할 수 없게 했기 때문에 확인하지 않아도 된다.
        var targetUser = fetchUserById(userContext.getUid());


        var comment = requestDto.toEntity(targetPost, targetUser, filteredContent);

        var newComment = commentRepository.save(comment);

        return new CommentResponseCommonDto(newComment);
    }

    private @NotNull Post fetchPostAndValidate(Long requestDto) {
        var targetPost = fetchPostById(requestDto);

        validateTargetPost(targetPost);
        return targetPost;
    }


    @Transactional
    public CommentResponseCommonDto createReply(ReplyCreateRequestServiceDto requestDto) {

        var targetPost = fetchPostAndValidate(requestDto.getPostId());

        var filteredContent = filteringCommentContent(requestDto.getContent());

        //이미 인증파트에서 유저 활성화 여부를 확인하여 비활성화된 유저는 로그인을 할 수 없게 했기 때문에 확인하지 않아도 된다.
        var targetUser = fetchUserById(userContext.getUid());

        var parent = fetchParentAndValidate(requestDto, targetPost);

        var comment = requestDto.toEntity(parent, targetUser, filteredContent);

        var newComment = commentRepository.save(comment);

        return new CommentResponseCommonDto(newComment);
    }

    private @NotNull Comment fetchParentAndValidate(ReplyCreateRequestServiceDto requestDto, Post targetPost) {
        var parent = fetchCommentById(requestDto.getParentId());

        validateParentComment(parent, targetPost);
        return parent;
    }


    @Transactional
    public CommentResponseCommonDto disabled(Long id) {

        var comment = fetchCommentById(id);

        validateCanCommentDisabled(comment);

        comment.setEnabled(false);

        var updatedComment = commentRepository.save(comment);

        return new CommentResponseCommonDto(updatedComment);
    }


//    @Transactional
//    public ReplyResponseCommonDto updateStatus(Long id, boolean status) {
//        var comment = fetchCommentById(id);
//
//        comment.setEnabled(status);
//        var updatedComment = commentRepository.save(comment);
//
//        return new ReplyResponseCommonDto(updatedComment);
//    }

    @Transactional
    public CommentResponseCommonDto delete(Long id) {
        var comment = fetchCommentById(id);

        if (!comment.getPost().isEnabled()) {
            throw new BusinessException("해당 게시글을 찾을 수 없습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        commentRepository.delete(comment);

        return new CommentResponseCommonDto(comment);
    }

    @Transactional
    public CommentResponseCommonDto update(CommentUpdateRequestServiceDto requestDto) {
        var targetComment = fetchCommentById(requestDto.getId());

        validateCanCommentUpdate(targetComment);

        if (targetComment.getContent().equals(requestDto.getContent())) {
            return new CommentResponseCommonDto(targetComment);
        }

        var filteredContent = filteringCommentContent(requestDto.getContent());

        targetComment.setContent(filteredContent);

        var updatedComment = commentRepository.save(targetComment);
        return new CommentResponseCommonDto(updatedComment);
    }


    @Transactional(readOnly = true)
    public Page<CommentResponseWithReplyCntCommonDto> getParentCommentWithPost(Long postId, Pageable pageable) {

        var targetPost = fetchPostById(postId);

        if (!targetPost.isEnabled()) {
            throw new BusinessException("해당 게시글을 찾을 수 없습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        var comments = commentRepository.findAllByPostAndParentIsNull(targetPost, pageable);

        if (comments.isEmpty()) {
            throw new BusinessException("해당 게시글에 댓글이 존재하지 않습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        var replyCounts = commentRepository.countReplyByParentIds(comments.map(Comment::getId).toSet());

        return comments.map(comment -> {
            var replyCount = replyCounts.stream()
                    .filter(replyCountVo -> replyCountVo.getId().equals(comment.getId()))
                    .findFirst()
                    .map(CommentReplyCountResponseDto::getReplyCount)
                    .orElse(0L);

            return new CommentResponseWithReplyCntCommonDto(comment, replyCount, userContext.isAdmin());
        });
    }

    @Transactional(readOnly = true)
    public Page<ReplyResponseCommonDto> getReply(Long parentId, Pageable pageable) {


        var parent = fetchCommentById(parentId);
        var targetPost = parent.getPost();

        if (!targetPost.isEnabled()) {
            throw new BusinessException("해당 게시글을 찾을 수 없습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }
        
        var comments = commentRepository.findAllByParent(parent, pageable);

        return comments.map((c) -> new ReplyResponseCommonDto(c, userContext.isAdmin()));
    }

    @Transactional(readOnly = true)
    public CommentResponseCommonDto getCommentById(Long id) {
        var comment = fetchCommentById(id);

        if (!comment.getPost().isEnabled()) {
            throw new BusinessException("해당 게시글을 찾을 수 없습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        return new CommentResponseCommonDto(comment, userContext.isAdmin());
    }


    private Post fetchPostById(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(postId.toString(), "게시글"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    private Comment fetchCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(commentId.toString(), "댓글"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }

    private String filteringCommentContent(String content) {
        var escapedContent = HtmlUtils.htmlEscape(content);

        // 욕설 필터링
        if (badWordFilter.isBadWord(escapedContent)) {
            throw new BusinessException("부적절한 용어가 포함되어있습니다.", BusinessErrorCode.NOT_VALID_ITEM);
        }

        return escapedContent;
    }

    private void validateParentComment(Comment parent, Post targetPost) {
        if (!parent.isEnabled()) {
            throw new BusinessException("이미 삭제된 댓글입니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        if (parent.isReply()) {
            throw new BusinessException("대댓글에는 댓글을 작성할 수 없습니다.", BusinessErrorCode.FORBIDDEN);
        }

        if (!Objects.equals(parent.getPost().getId(), targetPost.getId())) {
            throw new BusinessException("게시글에 해당하는 댓글만 작성할 수 있습니다.", BusinessErrorCode.FORBIDDEN);
        }
    }

    private void validateTargetPost(Post targetPost) {
        if (!targetPost.isEnabled()) {
            throw new BusinessException("해당 게시글을 찾을 수 없습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        if (!targetPost.getPostType().isCanAddComment()) {
            throw new BusinessException("해당 게시글에 댓글을 작성할 수 없습니다.", BusinessErrorCode.FORBIDDEN);
        }
    }

    private User fetchUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(
                        ExceptionMessageFormatter.createItemNotFoundMessage(userId, "유저"),
                        BusinessErrorCode.ITEM_NOT_FOUND));
    }


    private void validateCanCommentDisabled(Comment comment) {
        if (!comment.getPost().isEnabled()) {
            throw new BusinessException("해당 게시글을 찾을 수 없습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        if (!comment.isEnabled()) {
            throw new BusinessException("이미 삭제된 댓글입니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        if (!comment.getUser().getId().equals(userContext.getUid()) && !userContext.isAdmin()) {
            throw new BusinessException("타인의 댓글은 삭제할 수 없습니다.", BusinessErrorCode.FORBIDDEN);
        }

    }

    private void validateCanCommentUpdate(Comment targetComment) {
        if (!targetComment.getPost().isEnabled()) {
            throw new BusinessException("해당 게시글을 찾을 수 없습니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        if (!targetComment.isEnabled()) {
            throw new BusinessException("이미 삭제된 댓글입니다.", BusinessErrorCode.ITEM_NOT_FOUND);
        }

        if (!userContext.getUid().equals(targetComment.getUser().getId())) {
            throw new BusinessException("타인의 댓글은 수정할 수 없습니다.", BusinessErrorCode.FORBIDDEN);
        }
    }


}
