package com.springnote.api.tests.service;

import com.springnote.api.domain.comment.Comment;
import com.springnote.api.domain.comment.CommentRepository;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.post.PostRepository;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.user.User;
import com.springnote.api.domain.user.UserRepository;
import com.springnote.api.dto.comment.common.CommentResponseCommonDto;
import com.springnote.api.dto.comment.common.CommentResponseWithReplyCntCommonDto;
import com.springnote.api.dto.comment.common.ReplyResponseCommonDto;
import com.springnote.api.dto.comment.service.CommentCreateRequestServiceDto;
import com.springnote.api.dto.comment.service.CommentReplyCountResponseDto;
import com.springnote.api.dto.comment.service.CommentUpdateRequestServiceDto;
import com.springnote.api.dto.comment.service.ReplyCreateRequestServiceDto;
import com.springnote.api.dto.user.controller.UserSimpleResponseControllerDto;
import com.springnote.api.service.CommentService;
import com.springnote.api.testUtils.dataFactory.TestDataFactory;
import com.springnote.api.testUtils.template.ServiceTestTemplate;
import com.springnote.api.utils.badWord.BadWordFilter;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.exception.business.BusinessErrorCode;
import com.springnote.api.utils.exception.business.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static com.springnote.api.testUtils.dataFactory.post.PostTestDataFactory.createPost;
import static com.springnote.api.testUtils.dataFactory.postType.PostTypeTestDataFactory.createAddCommentOKPostType;
import static com.springnote.api.testUtils.dataFactory.user.UserTestDataFactory.createAdmin;
import static com.springnote.api.testUtils.dataFactory.user.UserTestDataFactory.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@DisplayName("Service Test - CommentService")
public class CommentServiceTest extends ServiceTestTemplate {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private PostRepository postRepository;

    @Mock
    private BadWordFilter badWordFilter;

    @Mock
    private UserContext userContext;

    @Mock
    private UserRepository userRepository;


    @DisplayName("commentService.create")
    @Nested
    class create {

        private static Stream<Arguments> provideUser() {
            return Stream.of(
                    Arguments.of(createUser(), "일반 유저"),
                    Arguments.of(createAdmin(), "관리자")
            );
        }

        @DisplayName("유저가 정상적인 댓글 정보를 제공하면,  댓글을 생성한다.")
        @MethodSource("provideUser")
        @ParameterizedTest(name = "{index} : {1} 이(가) 정상적인 댓글 정보를 제공하면, 댓글을 생성한다.")
        void create_success(User currentUser, String userCase) {
            // given
            var requestDto = CommentCreateRequestServiceDto.builder()
                    .postId(1L)
                    .content("content")
                    .ip("1.1.1.1")
                    .build();

            //주어진 post id로 post를 찾는다.
            var targetPost = createPost(createAddCommentOKPostType());

            doReturn(Optional.of(targetPost)).when(postRepository).findById(1L);

            // 댓글 내용을 필터링한다.
            doReturn(false).when(badWordFilter).isBadWord("content");

            // User Context내 현재 유저 UID를 조회하여 댓글 작성자로 설정한다.
            var currentUserUid = currentUser.getId();

            doReturn(currentUserUid).when(userContext).getUid();

            doReturn(Optional.of(currentUser)).when(userRepository).findById(currentUserUid);

            // 앞서 검증 및 설정한 정보로 댓글을 생성한다.
            var targetComment = Comment.builder()
                    .content("content")
                    .post(targetPost)
                    .user(currentUser)
                    .ip("1.1.1.1")
                    .isEnabled(true)
                    .build();

            var savedComment = Comment.builder()
                    .id(1L)
                    .content("content")
                    .post(targetPost)
                    .user(currentUser)
                    .ip("1.1.1.1")
                    .createdDate(testLocalDateTime())
                    .lastModifiedDate(testLocalDateTime())
                    .isEnabled(true)
                    .build();


            doReturn(savedComment).when(commentRepository).save(targetComment);
            // when
            var result = commentService.create(requestDto);

            // then
            var expected = CommentResponseCommonDto.builder()
                    .id(1L)
                    .content("content")
                    .postId(1L)
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(currentUser.getId())
                            .name(currentUser.getName())
                            .email(currentUser.getEmail())
                            .isAdmin(currentUser.isAdmin())
                            .isEnabled(currentUser.isEnabled())
                            .profileImg(currentUser.getProfileImg())
                            .build())
                    .createdDate(savedComment.getCreatedDate())
                    .lastModifiedDate(savedComment.getLastModifiedDate())
                    .enabled(true)
                    .build();

            assertEquals(expected, result);
            verify(commentRepository).save(targetComment);
            verify(postRepository).findById(1L);
            verify(badWordFilter).isBadWord("content");
            verify(userContext).getUid();
            verify(userRepository).findById(currentUserUid);
        }

        private static Stream<Arguments> provideUserWithErrorPost() {
            var notEnabledPost = createPost(false);

            var notCanAddCommentPost = createPost(PostType.builder().isCanAddComment(false).build());

            return Stream.of(
                    Arguments.of(createUser(), "일반 유저", notEnabledPost, "비활성 된 게시글", BusinessErrorCode.ITEM_NOT_FOUND),
                    Arguments.of(createAdmin(), "관리자", notEnabledPost, "비활성 된 게시글", BusinessErrorCode.ITEM_NOT_FOUND),
                    Arguments.of(createUser(), "일반 유저", notCanAddCommentPost, "댓글 작성 불가능한 게시글", BusinessErrorCode.FORBIDDEN),
                    Arguments.of(createAdmin(), "관리자", notCanAddCommentPost, "댓글 작성 불가능한 게시글", BusinessErrorCode.FORBIDDEN)
            );
        }

        @DisplayName("유저가 올바르지 않은 Post 에 댓글 생성을 요청하면, 댓글 생성에 실패한다.")
        @MethodSource("provideUserWithErrorPost")
        @ParameterizedTest(name = "{index} : {1} 이(가) {3} 상태의 Post 에 댓글 생성을 요청하면, 댓글 생성에 실패한다.")
        void create_failWithErrorPost(User currentUser, String userCase, Post errorPost, String errorMessage, BusinessErrorCode expectedErrorCode) {
            // given
            var requestDto = CommentCreateRequestServiceDto.builder()
                    .postId(1L)
                    .content("content")
                    .ip("1.1.1.1")
                    .build();

            //주어진 post id로 post를 찾는다.
            doReturn(Optional.of(errorPost)).when(postRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.create(requestDto));

            // then
            assertEquals(expectedErrorCode, result.getErrorCode());
            verify(postRepository).findById(1L);
        }

        //따로 유저 케이스를 나누지 않는 이유는, 비속어 필터링은 모든 유저에게 적용되기 때문이다.
        @DisplayName("유저가 정상적인 비속어가 담긴 댓글 정보를 제공하면, 댓글 생성에 실패한다.")
        @Test
        void create_failWithBadContent() {
            // given
            var requestDto = CommentCreateRequestServiceDto.builder()
                    .postId(1L)
                    .content("비속어")
                    .ip("1.1.1.1")
                    .build();

            //주어진 post id로 post를 찾는다.
            var targetPost = createPost(createAddCommentOKPostType());
            doReturn(Optional.of(targetPost)).when(postRepository).findById(1L);

            // 댓글 내용을 필터링한다. 이때 비속어가 포함되어있으므로 예외가 발생해야한다.
            doReturn(true).when(badWordFilter).isBadWord(ArgumentMatchers.eq(requestDto.getContent()));

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.create(requestDto));

            // then
            assertEquals(BusinessErrorCode.NOT_VALID_ITEM, result.getErrorCode());
            verify(postRepository).findById(1L);
            verify(badWordFilter).isBadWord(ArgumentMatchers.eq(requestDto.getContent()));

        }

    }

    @DisplayName("commentService.createReply")
    @Nested
    class createReply {

        private static Stream<Arguments> provideUser() {
            return Stream.of(
                    Arguments.of(createUser(), "일반 유저"),
                    Arguments.of(createAdmin(), "관리자")
            );
        }

        @DisplayName("유저가 정상적인 대댓글 정보를 제공하면,  대댓글을 생성한다.")
        @MethodSource("provideUser")
        @ParameterizedTest(name = "{index} : {1} 이(가) 정상적인 댓글 정보를 제공하면, 댓글을 생성한다.")
        void createReply_success(User currentUser, String userCase) {
            // given
            var requestDto = ReplyCreateRequestServiceDto.builder()
                    .postId(1L)
                    .content("content")
                    .ip("1.1.1.1")
                    .parentId(1L)
                    .build();

            //주어진 post id로 post를 찾는다.
            var targetPost = createPost(createAddCommentOKPostType());
            doReturn(Optional.of(targetPost)).when(postRepository).findById(1L);

            // 댓글 내용을 필터링한다.
            doReturn(false).when(badWordFilter).isBadWord("content");

            // User Context내 현재 유저 UID를 조회하여 댓글 작성자로 설정한다.
            var currentUserUid = currentUser.getId();

            doReturn(currentUserUid).when(userContext).getUid();

            doReturn(Optional.of(currentUser)).when(userRepository).findById(currentUserUid);

            //부모 댓글을 찾고 검증한다.
            var parentComment = Comment.builder()
                    .id(requestDto.getParentId())
                    .content("parent")
                    .isEnabled(true)
                    .post(targetPost)
                    .build();

            doReturn(Optional.of(parentComment)).when(commentRepository).findById(requestDto.getParentId());

            // 앞서 검증 및 설정한 정보로 댓글을 생성한다.
            var targetComment = Comment.builder()
                    .content("content")
                    .post(targetPost)
                    .user(currentUser)
                    .ip("1.1.1.1")
                    .isEnabled(true)
                    .parent(parentComment)
                    .build();

            var savedComment = Comment.builder()
                    .id(1L)
                    .content("content")
                    .post(targetPost)
                    .user(currentUser)
                    .ip("1.1.1.1")
                    .createdDate(testLocalDateTime())
                    .lastModifiedDate(testLocalDateTime())
                    .isEnabled(true)
                    .parent(parentComment)
                    .build();


            doReturn(savedComment).when(commentRepository).save(targetComment);
            // when
            var result = commentService.createReply(requestDto);

            // then
            var expected = CommentResponseCommonDto.builder()
                    .id(1L)
                    .content("content")
                    .postId(1L)
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(currentUser.getId())
                            .name(currentUser.getName())
                            .email(currentUser.getEmail())
                            .isAdmin(currentUser.isAdmin())
                            .isEnabled(currentUser.isEnabled())
                            .profileImg(currentUser.getProfileImg())
                            .build())
                    .createdDate(savedComment.getCreatedDate())
                    .lastModifiedDate(savedComment.getLastModifiedDate())
                    .enabled(true)
                    .build();

            assertEquals(expected, result);
            verify(commentRepository).save(targetComment);
            verify(postRepository).findById(1L);
            verify(badWordFilter).isBadWord("content");
            verify(userContext).getUid();
            verify(userRepository).findById(currentUserUid);
            verify(commentRepository).findById(requestDto.getParentId());
        }

        private static Stream<Arguments> provideUserWithErrorPost() {
            var notEnabledPost = createPost(false);

            var notCanAddCommentPost = createPost(PostType.builder().isCanAddComment(false).build());

            return Stream.of(
                    Arguments.of(createUser(), "일반 유저", notEnabledPost, "비활성 된 게시글", BusinessErrorCode.ITEM_NOT_FOUND),
                    Arguments.of(createAdmin(), "관리자", notEnabledPost, "비활성 된 게시글", BusinessErrorCode.ITEM_NOT_FOUND),
                    Arguments.of(createUser(), "일반 유저", notCanAddCommentPost, "댓글 작성 불가능한 게시글", BusinessErrorCode.FORBIDDEN),
                    Arguments.of(createAdmin(), "관리자", notCanAddCommentPost, "댓글 작성 불가능한 게시글", BusinessErrorCode.FORBIDDEN)
            );
        }

        @DisplayName("유저가 올바르지 않은 Post 에 대댓글 생성을 요청하면, 대댓글 생성에 실패한다.")
        @MethodSource("provideUserWithErrorPost")
        @ParameterizedTest(name = "{index} : {1} 이(가) {3} 상태의 Post 에 댓글 생성을 요청하면, 대댓글 생성에 실패한다.")
        void createReply_failWithErrorPost(User currentUser, String userCase, Post errorPost, String errorMessage, BusinessErrorCode expectedErrorCode) {
            // given
            var requestDto = ReplyCreateRequestServiceDto.builder()
                    .postId(1L)
                    .content("content")
                    .ip("1.1.1.1")
                    .parentId(1L)
                    .build();

            //주어진 post id로 post를 찾는다.
            doReturn(Optional.of(errorPost)).when(postRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.createReply(requestDto));

            // then
            assertEquals(expectedErrorCode, result.getErrorCode());
            verify(postRepository).findById(1L);
        }

        //따로 유저 케이스를 나누지 않는 이유는, 비속어 필터링은 모든 유저에게 적용되기 때문이다.
        @DisplayName("유저가 정상적인 비속어가 담긴 대댓글 정보를 제공하면, 대댓글 생성에 실패한다.")
        @Test
        void create_failWithBadContent() {
            // given
            var requestDto = ReplyCreateRequestServiceDto.builder()
                    .postId(1L)
                    .content("비속어")
                    .ip("1.1.1.1")
                    .parentId(1L)
                    .build();

            //주어진 post id로 post를 찾는다.
            var targetPost = createPost(createAddCommentOKPostType());
            doReturn(Optional.of(targetPost)).when(postRepository).findById(1L);

            // 댓글 내용을 필터링한다. 이때 비속어가 포함되어있으므로 예외가 발생해야한다.
            doReturn(true).when(badWordFilter).isBadWord(ArgumentMatchers.eq(requestDto.getContent()));

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.createReply(requestDto));

            // then
            assertEquals(BusinessErrorCode.NOT_VALID_ITEM, result.getErrorCode());
            verify(postRepository).findById(1L);
            verify(badWordFilter).isBadWord(ArgumentMatchers.eq(requestDto.getContent()));

        }

        private static Stream<Arguments> provideUserWithErrorParents() {
            var targetPost = createPost(createAddCommentOKPostType());

            var notEnabledComment = Comment.builder()
                    .id(1L)
                    .content("parent")
                    .isEnabled(false)
                    .post(targetPost)
                    .build();

            var replyComment = Comment.builder()
                    .id(1L)
                    .content("parent")
                    .isEnabled(true)
                    .post(targetPost)
                    .parent(Comment.builder().id(1L).build())
                    .build();

            var notSamePostComment = Comment.builder()
                    .id(1L)
                    .content("parent")
                    .isEnabled(true)
                    .post(Post.builder().id(2L).build())
                    .build();

            return Stream.of(
                    Arguments.of(createUser(), "일반 유저", notEnabledComment, "비활성 된 부모 댓글", BusinessErrorCode.ITEM_NOT_FOUND),
                    Arguments.of(createAdmin(), "관리자", notEnabledComment, "비활성 된 부모 댓글", BusinessErrorCode.ITEM_NOT_FOUND),
                    Arguments.of(createUser(), "일반 유저", replyComment, "대댓글이 부모 댓글", BusinessErrorCode.FORBIDDEN),
                    Arguments.of(createAdmin(), "관리자", replyComment, "대댓글이 부모 댓글", BusinessErrorCode.FORBIDDEN),
                    Arguments.of(createUser(), "일반 유저", notSamePostComment, "게시글이 다른 부모 댓글", BusinessErrorCode.FORBIDDEN),
                    Arguments.of(createAdmin(), "관리자", notSamePostComment, "게시글이 다른 부모 댓글", BusinessErrorCode.FORBIDDEN)
            );
        }

        @DisplayName("유저가 정상적이지 않은 댓글에 대댓글을 작성하려하면,  대댓글을 생성에 실패한다.")
        @MethodSource("provideUserWithErrorParents")
        @ParameterizedTest(name = "{index} : {1} 이(가) {3} 상태의 부모 댓글에 대댓글을 작성하려하면, 대댓글 생성에 실패한다.")
        void createReply_failWithParentComment(User currentUser, String userCase, Comment errorParent, String errorMessage, BusinessErrorCode expectedErrorCode) {
            // given
            var requestDto = ReplyCreateRequestServiceDto.builder()
                    .postId(1L)
                    .content("content")
                    .ip("1.1.1.1")
                    .parentId(1L)
                    .build();

            //주어진 post id로 post를 찾는다.
            var targetPost = createPost(createAddCommentOKPostType());

            doReturn(Optional.of(targetPost)).when(postRepository).findById(1L);

            // 댓글 내용을 필터링한다.
            doReturn(false).when(badWordFilter).isBadWord("content");

            // User Context내 현재 유저 UID를 조회하여 댓글 작성자로 설정한다.
            var currentUserUid = currentUser.getId();

            doReturn(currentUserUid).when(userContext).getUid();

            doReturn(Optional.of(currentUser)).when(userRepository).findById(currentUserUid);

            //부모 댓글을 찾고 검증한다.
            doReturn(Optional.of(errorParent)).when(commentRepository).findById(errorParent.getId());

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.createReply(requestDto));

            // then
            assertEquals(expectedErrorCode, result.getErrorCode());
            verify(postRepository).findById(1L);
            verify(badWordFilter).isBadWord("content");
            verify(userContext).getUid();
            verify(userRepository).findById(currentUserUid);
            verify(commentRepository).findById(requestDto.getParentId());
        }

    }

    @DisplayName("commentService.disabled")
    @Nested
    class disabled {

        private static Stream<Arguments> provideUser() {
            return Stream.of(
                    Arguments.of(createUser(), "일반 유저"),
                    Arguments.of(createAdmin(), "관리자")
            );
        }

        @DisplayName("정상적인 댓글 ID를 제공하면, 댓글을 비활성화한다.")
        @MethodSource("provideUser")
        @ParameterizedTest(name = "{index} : {1} 이(가) 정상적인 댓글 ID를 제공하면, 댓글을 비활성화한다.")
        void disabled_success(User currentUser, String userCase) {
            // given
            var targetComment = Comment.builder()
                    .id(1L)
                    .isEnabled(true)
                    .post(createPost())
                    .user(currentUser)
                    .build();

            var updatedComment = Comment.builder()
                    .id(1L)
                    .isEnabled(false)
                    .post(createPost())
                    .user(currentUser)
                    .build();

            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);
            doReturn(currentUser.getId()).when(userContext).getUid();
            doReturn(updatedComment).when(commentRepository).save(targetComment);

            // when
            var result = commentService.disabled(1L);

            // then
            var expected = CommentResponseCommonDto.builder()
                    .id(1L)
                    .content(targetComment.getContent())
                    .postId(targetComment.getPost().getId())
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(targetComment.getUser().getId())
                            .name(targetComment.getUser().getName())
                            .email(targetComment.getUser().getEmail())
                            .isAdmin(targetComment.getUser().isAdmin())
                            .isEnabled(targetComment.getUser().isEnabled())
                            .profileImg(targetComment.getUser().getProfileImg())
                            .build())
                    .createdDate(targetComment.getCreatedDate())
                    .lastModifiedDate(targetComment.getLastModifiedDate())
                    .enabled(false)
                    .build();

            verify(commentRepository).findById(1L);
            verify(commentRepository).save(targetComment);
            verify(userContext).getUid();
        }

        //유저 권한 상관 없음
        @DisplayName("존재하지 않는 댓글 ID를 제공하면, 댓글 비활성화에 실패한다.")
        @Test
        void disabled_failWithNotExistsComment() {
            // given
            doReturn(Optional.empty()).when(commentRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.disabled(1L));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(commentRepository).findById(1L);
        }

        //유저 권한 상관 없음
        @DisplayName("유저가 이미 비활성화된 댓글 ID를 제공하면, 댓글 비활성화에 실패한다.")
        @Test
        void disabled_failWithAlreadyDisabledComment() {
            // given
            var targetComment = Comment.builder()
                    .id(1L)
                    .isEnabled(false)
                    .post(createPost())
                    .build();

            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.disabled(1L));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(commentRepository).findById(1L);
        }

        //유저 권한 상관 없음
        @DisplayName("유저가 비활성화된 포스트의 댓글 ID를 제공하면, 댓글 비활성화에 실패한다.")
        @Test
        void disabled_failWithDisabledPost() {
            // given
            var targetComment = Comment.builder()
                    .id(1L)
                    .isEnabled(true)
                    .post(Post.builder().isEnabled(false).build())
                    .build();

            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.disabled(1L));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(commentRepository).findById(1L);
        }

        @DisplayName("유저가 다른 유저의 댓글 ID를 제공하면, 댓글 비활성화에 실패한다.")
        @Test
        void disabled_failWithDifferentUser() {
            // given
            var targetComment = Comment.builder()
                    .id(1L)
                    .isEnabled(true)
                    .post(createPost())
                    .user(User.builder().id("notSame").build())
                    .build();
            var currentUser = createUser();
            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);
            doReturn(currentUser.getId()).when(userContext).getUid();
            doReturn(currentUser.isAdmin()).when(userContext).isAdmin();

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.disabled(1L));

            // then
            assertEquals(BusinessErrorCode.FORBIDDEN, result.getErrorCode());
            verify(commentRepository).findById(1L);
            verify(userContext).getUid();
        }

        @DisplayName("관리자가 다른 유저의 댓글 ID를 제공하면, 댓글 비활성화에 성공한다.")
        @Test
        void disabled_successWithAdmin() {
            // given
            var targetComment = Comment.builder()
                    .id(1L)
                    .isEnabled(true)
                    .post(createPost())
                    .user(User.builder().id("notSame").build())
                    .build();

            var savedComment = Comment.builder()
                    .id(1L)
                    .isEnabled(false)
                    .post(createPost())
                    .user(User.builder().id("notSame").build())
                    .build();

            var currentUser = createAdmin();

            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);
            doReturn(currentUser.getId()).when(userContext).getUid();
            doReturn(currentUser.isAdmin()).when(userContext).isAdmin();
            doReturn(savedComment).when(commentRepository).save(targetComment);

            // when
            commentService.disabled(1L);

            // then
            verify(commentRepository).findById(1L);
            verify(commentRepository).save(targetComment);
            verify(userContext).getUid();
            verify(userContext).isAdmin();
            verify(userContext).getUid();
        }

    }

    @DisplayName("commentService.delete")
    @Nested
    class delete {

        @DisplayName("정상적인 댓글 ID를 제공하면, 댓글을 삭제한다.")
        @Test
        void delete_success() {
            // given
            var currentUser = createUser();

            var targetComment = Comment.builder()
                    .id(1L)
                    .post(createPost())
                    .user(currentUser)
                    .build();

            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);

            // when
            commentService.delete(1L);

            // then
            verify(commentRepository).findById(1L);
            verify(commentRepository).delete(targetComment);

        }

        @DisplayName("존재하지 않는 댓글 ID를 제공하면, 댓글 삭제에 실패한다.")
        @Test
        void delete_failWithNotExistsComment() {
            // given
            doReturn(Optional.empty()).when(commentRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.delete(1L));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(commentRepository).findById(1L);
        }
    }

    @DisplayName("commentService.update")
    @Nested
    class update {


        @DisplayName("정상적인 댓글 ID와 내용을 제공하면, 댓글을 수정한다.")
        @Test
        void update_success() {
            // given
            var updateRequestDto = CommentUpdateRequestServiceDto.builder()
                    .id(1L)
                    .content("updated content")
                    .build();

            var currentUser = createUser();

            var targetComment = Comment.builder()
                    .id(1L)
                    .content("content")
                    .post(createPost())
                    .user(currentUser)
                    .isEnabled(true)
                    .build();

            var updatedComment = Comment.builder()
                    .id(1L)
                    .content("updated content")
                    .post(createPost())
                    .user(currentUser)
                    .isEnabled(true)
                    .build();

            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);
            doReturn(currentUser.getId()).when(userContext).getUid();
            doReturn(updatedComment).when(commentRepository).save(updatedComment);
            doReturn(false).when(badWordFilter).isBadWord("updated content");

            // when
            var result = commentService.update(updateRequestDto);

            // then
            var expected = CommentResponseCommonDto.builder()
                    .id(1L)
                    .content("updated content")
                    .postId(targetComment.getPost().getId())
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(targetComment.getUser().getId())
                            .name(targetComment.getUser().getName())
                            .email(targetComment.getUser().getEmail())
                            .isAdmin(targetComment.getUser().isAdmin())
                            .isEnabled(targetComment.getUser().isEnabled())
                            .profileImg(targetComment.getUser().getProfileImg())
                            .build())
                    .createdDate(targetComment.getCreatedDate())
                    .lastModifiedDate(targetComment.getLastModifiedDate())
                    .enabled(true)
                    .build();

            assertEquals(expected, result);
            verify(commentRepository).findById(1L);
            verify(commentRepository).save(updatedComment);
            verify(userContext).getUid();
            verify(badWordFilter).isBadWord("updated content");
        }

        @DisplayName("존재하지 않는 댓글 ID를 제공하면, 댓글 수정에 실패한다.")
        @Test
        void update_failWithNotExistsComment() {
            // given
            var updateRequestDto = CommentUpdateRequestServiceDto.builder()
                    .id(1L)
                    .content("updated content")
                    .build();

            doReturn(Optional.empty()).when(commentRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.update(updateRequestDto));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(commentRepository).findById(1L);
        }

        @DisplayName("다른 유저의 댓글 ID를 제공하면, 댓글 수정에 실패한다.")
        @Test
        void update_failWithDifferentUser() {
            // given
            var updateRequestDto = CommentUpdateRequestServiceDto.builder()
                    .id(1L)
                    .content("updated content")
                    .build();

            var targetComment = Comment.builder()
                    .id(1L)
                    .content("content")
                    .post(createPost())
                    .user(User.builder().id("notSame").build())
                    .isEnabled(true)
                    .build();
            var currentUser = createUser();
            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);
            doReturn(currentUser.getId()).when(userContext).getUid();

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.update(updateRequestDto));

            // then
            assertEquals(BusinessErrorCode.FORBIDDEN, result.getErrorCode());
            verify(commentRepository).findById(1L);
            verify(userContext).getUid();
        }

        @DisplayName("업데이트 전과 동일한 내용을 제공하면, 업데이트 하지 않고 기존 내용을 반환한다.")
        @Test
        void update_failWithSameContent() {
            // given
            var updateRequestDto = CommentUpdateRequestServiceDto.builder()
                    .id(1L)
                    .content("content")
                    .build();

            var currentUser = createUser();

            var targetComment = Comment.builder()
                    .id(1L)
                    .content("content")
                    .post(createPost())
                    .user(currentUser)
                    .isEnabled(true)
                    .build();


            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);
            doReturn(currentUser.getId()).when(userContext).getUid();

            // when
            var result = commentService.update(updateRequestDto);

            // then
            var expected = CommentResponseCommonDto.builder()
                    .id(1L)
                    .content("content")
                    .postId(targetComment.getPost().getId())
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(targetComment.getUser().getId())
                            .name(targetComment.getUser().getName())
                            .email(targetComment.getUser().getEmail())
                            .isAdmin(targetComment.getUser().isAdmin())
                            .isEnabled(targetComment.getUser().isEnabled())
                            .profileImg(targetComment.getUser().getProfileImg())
                            .build())
                    .createdDate(targetComment.getCreatedDate())
                    .lastModifiedDate(targetComment.getLastModifiedDate())
                    .enabled(true)
                    .build();

            assertEquals(expected, result);
            verify(commentRepository).findById(1L);
            verify(userContext).getUid();
        }

        @DisplayName("댓글에 비속어가 포함되어 있으면, 댓글 수정에 실패한다.")
        @Test
        void update_failWithBadContent() {
            // given
            var updateRequestDto = CommentUpdateRequestServiceDto.builder()
                    .id(1L)
                    .content("비속어")
                    .build();

            var currentUser = createUser();

            var targetComment = Comment.builder()
                    .id(1L)
                    .content("content")
                    .post(createPost())
                    .user(currentUser)
                    .isEnabled(true)
                    .build();


            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);
            doReturn(currentUser.getId()).when(userContext).getUid();
            doReturn(true).when(badWordFilter).isBadWord("비속어");

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.update(updateRequestDto));

            // then
            assertEquals(BusinessErrorCode.NOT_VALID_ITEM, result.getErrorCode());
            verify(commentRepository).findById(1L);
            verify(userContext).getUid();
            verify(badWordFilter).isBadWord("비속어");
        }

    }

    @DisplayName("commentService.getParentCommentWithPost")
    @Nested
    class getParentCommentWithPost {

        private static Stream<Arguments> provideUser() {
            return Stream.of(
                    Arguments.of(createUser(), "일반 유저"),
                    Arguments.of(createAdmin(), "관리자")
            );
        }

        @DisplayName("부모 댓글 ID를 제공하면, 부모 댓글을 반환한다.")
        @MethodSource("provideUser")
        @ParameterizedTest(name = "{index} : {1} 이(가) 부모 댓글 ID를 제공하면, 부모 댓글을 반환한다.")
        void getParentCommentWithPost_success(User currentUser, String userCase) {
            // given
            var targetPost = createPost(createAddCommentOKPostType());

            var parentComment = Comment.builder()
                    .id(1L)
                    .content("parent")
                    .isEnabled(true)
                    .post(targetPost)
                    .user(createUser())
                    .build();

            var replyCount = new CommentReplyCountResponseDto(1L, 1L);

            doReturn(Optional.of(targetPost)).when(postRepository).findById(1L);
            doReturn(TestDataFactory.createPageObject(parentComment)).when(commentRepository).findAllByPostAndParentIsNull(ArgumentMatchers.eq(targetPost), ArgumentMatchers.any(Pageable.class));
            doReturn(List.of(replyCount)).when(commentRepository).countReplyByParentIds(ArgumentMatchers.eq(Set.of(1L)));
            doReturn(currentUser.isAdmin()).when(userContext).isAdmin();

            // when
            var result = commentService.getParentCommentWithPost(1L, TestDataFactory.getMockPageable());

            // then
            var expected = CommentResponseWithReplyCntCommonDto.builder()
                    .id(1L)
                    .content("parent")
                    .postId(targetPost.getId())
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(parentComment.getUser().getId())
                            .name(parentComment.getUser().getName())
                            .email(parentComment.getUser().getEmail())
                            .isAdmin(parentComment.getUser().isAdmin())
                            .isEnabled(parentComment.getUser().isEnabled())
                            .profileImg(parentComment.getUser().getProfileImg())
                            .build())
                    .createdDate(parentComment.getCreatedDate())
                    .lastModifiedDate(parentComment.getLastModifiedDate())
                    .replyCount(1L)
                    .enabled(true)
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(commentRepository).findAllByPostAndParentIsNull(ArgumentMatchers.eq(targetPost), ArgumentMatchers.any(Pageable.class));
            verify(commentRepository).countReplyByParentIds(ArgumentMatchers.eq(Set.of(1L)));
            verify(postRepository).findById(1L);
            verify(userContext).isAdmin();
        }

        private static Stream<Arguments> provideUserWithFilterContent() {
            return Stream.of(
                    Arguments.of(createUser(), "일반 유저", "삭제된 댓글입니다.", "숨겨진다."),
                    Arguments.of(createAdmin(), "관리자", "parent", "숨겨지지 않는다.")
            );
        }

        @DisplayName("비활성화된 부모댓글이 주어지면, 권한에 따라 댓글 내용이 필터링 된다.")
        @MethodSource("provideUserWithFilterContent")
        @ParameterizedTest(name = "{index} : {1} 이(가) 비활성화된 부모댓글이 주어지면, {2}")
        void getParentCommentWithPost_successWithDisabledComment(User currentUser, String userCase, String filteredContent, String contentCase) {
            // given
            var targetPost = createPost(createAddCommentOKPostType());

            var parentComment = Comment.builder()
                    .id(1L)
                    .content("parent")
                    .isEnabled(false)
                    .post(targetPost)
                    .user(createUser())
                    .build();

            var replyCount = new CommentReplyCountResponseDto(1L, 1L);

            doReturn(Optional.of(targetPost)).when(postRepository).findById(1L);
            doReturn(TestDataFactory.createPageObject(parentComment)).when(commentRepository).findAllByPostAndParentIsNull(ArgumentMatchers.eq(targetPost), ArgumentMatchers.any(Pageable.class));
            doReturn(List.of(replyCount)).when(commentRepository).countReplyByParentIds(ArgumentMatchers.eq(Set.of(1L)));
            doReturn(currentUser.isAdmin()).when(userContext).isAdmin();

            // when
            var result = commentService.getParentCommentWithPost(1L, TestDataFactory.getMockPageable());

            // then
            var expected = CommentResponseWithReplyCntCommonDto.builder()
                    .id(1L)
                    .content(filteredContent)
                    .postId(targetPost.getId())
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(parentComment.getUser().getId())
                            .name(parentComment.getUser().getName())
                            .email(parentComment.getUser().getEmail())
                            .isAdmin(parentComment.getUser().isAdmin())
                            .isEnabled(parentComment.getUser().isEnabled())
                            .profileImg(parentComment.getUser().getProfileImg())
                            .build())
                    .createdDate(parentComment.getCreatedDate())
                    .lastModifiedDate(parentComment.getLastModifiedDate())
                    .replyCount(1L)
                    .enabled(false)
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(commentRepository).findAllByPostAndParentIsNull(ArgumentMatchers.eq(targetPost), ArgumentMatchers.any(Pageable.class));
            verify(commentRepository).countReplyByParentIds(ArgumentMatchers.eq(Set.of(1L)));
            verify(postRepository).findById(1L);
            verify(userContext).isAdmin();
        }


        @DisplayName("존재하지 않는 Post ID를 제공하면, 부모 댓글 조회에 실패한다.")
        @Test
        void getParentCommentWithPost_failWithNotExistsPost() {
            // given
            doReturn(Optional.empty()).when(postRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.getParentCommentWithPost(1L, TestDataFactory.getMockPageable()));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(postRepository).findById(1L);
        }

        @DisplayName("댓글이 없는 Post ID를 제공하면, 부모 댓글 조회에 실패한다.")
        @Test
        void getParentCommentWithPost_failWithNotExistsComment() {
            // given
            var targetPost = createPost(createAddCommentOKPostType());

            doReturn(Optional.of(targetPost)).when(postRepository).findById(1L);
            doReturn(TestDataFactory.createPageObject(List.of())).when(commentRepository).findAllByPostAndParentIsNull(ArgumentMatchers.eq(targetPost), ArgumentMatchers.any(Pageable.class));

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.getParentCommentWithPost(1L, TestDataFactory.getMockPageable()));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(postRepository).findById(1L);
            verify(commentRepository).findAllByPostAndParentIsNull(ArgumentMatchers.eq(targetPost), ArgumentMatchers.any(Pageable.class));

        }

        @DisplayName("대댓글 이 없는 부모 댓글이 주 어지면, 대댓글 수는 0이다.")
        @Test
        void getParentCommentWithPost_successWithNoReply() {
            // given
            var targetPost = createPost(createAddCommentOKPostType());
            var currentUser = createUser();
            var parentComment = Comment.builder()
                    .id(1L)
                    .content("parent")
                    .isEnabled(true)
                    .post(targetPost)
                    .user(createUser())
                    .build();

            doReturn(Optional.of(targetPost)).when(postRepository).findById(1L);
            doReturn(TestDataFactory.createPageObject(parentComment)).when(commentRepository).findAllByPostAndParentIsNull(ArgumentMatchers.eq(targetPost), ArgumentMatchers.any(Pageable.class));
            doReturn(List.of()).when(commentRepository).countReplyByParentIds(ArgumentMatchers.eq(Set.of(1L)));
            doReturn(currentUser.isAdmin()).when(userContext).isAdmin();
            // when
            var result = commentService.getParentCommentWithPost(1L, TestDataFactory.getMockPageable());

            // then
            var expected = CommentResponseWithReplyCntCommonDto.builder()
                    .id(1L)
                    .content("parent")
                    .postId(targetPost.getId())
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(parentComment.getUser().getId())
                            .name(parentComment.getUser().getName())
                            .email(parentComment.getUser().getEmail())
                            .isAdmin(parentComment.getUser().isAdmin())
                            .isEnabled(parentComment.getUser().isEnabled())
                            .profileImg(parentComment.getUser().getProfileImg())
                            .build())
                    .createdDate(parentComment.getCreatedDate())
                    .lastModifiedDate(parentComment.getLastModifiedDate())
                    .replyCount(0L)
                    .enabled(true)
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(commentRepository).findAllByPostAndParentIsNull(ArgumentMatchers.eq(targetPost), ArgumentMatchers.any(Pageable.class));
            verify(commentRepository).countReplyByParentIds(ArgumentMatchers.eq(Set.of(1L)));
            verify(postRepository).findById(1L);
            verify(userContext).isAdmin();
        }
    }

    @DisplayName("commentService.getReply")
    @Nested
    class getReply {

        private static Stream<Arguments> provideUser() {
            return Stream.of(
                    Arguments.of(createUser(), "일반 유저"),
                    Arguments.of(createAdmin(), "관리자")
            );
        }


        @DisplayName("부모 댓글 ID를 제공하면, 대댓글을 반환한다.")
        @MethodSource("provideUser")
        @ParameterizedTest(name = "{index} : {1} 이(가) 부모 댓글 ID를 제공하면, 대댓글을 반환한다.")
        void getReply_success(User currentUser, String userCase) {
            // given
            var targetPost = createPost(createAddCommentOKPostType());

            var parentComment = Comment.builder()
                    .id(1L)
                    .content("parent")
                    .isEnabled(true)
                    .post(targetPost)
                    .user(createUser())
                    .build();

            var replyComment = Comment.builder()
                    .id(2L)
                    .content("reply")
                    .isEnabled(true)
                    .post(targetPost)
                    .user(createUser())
                    .parent(parentComment)
                    .build();

            doReturn(Optional.of(parentComment)).when(commentRepository).findById(1L);
            doReturn(TestDataFactory.createPageObject(replyComment)).when(commentRepository).findAllByParent(ArgumentMatchers.eq(parentComment), ArgumentMatchers.any(Pageable.class));
            doReturn(currentUser.isAdmin()).when(userContext).isAdmin();

            // when
            var result = commentService.getReply(1L, TestDataFactory.getMockPageable());

            // then
            var expected = ReplyResponseCommonDto.builder()
                    .id(2L)
                    .content("reply")
                    .postId(targetPost.getId())
                    .parentId(1L)
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(replyComment.getUser().getId())
                            .name(replyComment.getUser().getName())
                            .email(replyComment.getUser().getEmail())
                            .isAdmin(replyComment.getUser().isAdmin())
                            .isEnabled(replyComment.getUser().isEnabled())
                            .profileImg(replyComment.getUser().getProfileImg())
                            .build())
                    .createdDate(replyComment.getCreatedDate())
                    .lastModifiedDate(replyComment.getLastModifiedDate())
                    .enabled(true)
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(commentRepository).findAllByParent(ArgumentMatchers.eq(parentComment), ArgumentMatchers.any(Pageable.class));
            verify(commentRepository).findById(1L);
            verify(userContext).isAdmin();
        }

        private static Stream<Arguments> provideUserWithFilterContent() {
            return Stream.of(
                    Arguments.of(createUser(), "일반 유저", "삭제된 댓글입니다.", "숨겨진다."),
                    Arguments.of(createAdmin(), "관리자", "reply", "숨겨지지 않는다.")
            );
        }

        @DisplayName("비활성화된 댓글이 주어지면, 권한에 따라 대댓글 내용이 필터링 된다.")
        @MethodSource("provideUserWithFilterContent")
        @ParameterizedTest(name = "{index} : {1} 이(가) 비활성화된 대댓글이 주어지면, {2}")
        void getReply_filterWithPermission(User currentUser, String userCase, String filteredContent, String contentCase) {
            // given
            var targetPost = createPost(createAddCommentOKPostType());

            var parentComment = Comment.builder()
                    .id(1L)
                    .content("parent")
                    .isEnabled(true)
                    .post(targetPost)
                    .user(createUser())
                    .build();

            var replyComment = Comment.builder()
                    .id(2L)
                    .content("reply")
                    .isEnabled(false)
                    .post(targetPost)
                    .user(createUser())
                    .parent(parentComment)
                    .build();

            doReturn(Optional.of(parentComment)).when(commentRepository).findById(1L);
            doReturn(TestDataFactory.createPageObject(replyComment)).when(commentRepository).findAllByParent(ArgumentMatchers.eq(parentComment), ArgumentMatchers.any(Pageable.class));
            doReturn(currentUser.isAdmin()).when(userContext).isAdmin();

            // when
            var result = commentService.getReply(1L, TestDataFactory.getMockPageable());

            // then
            var expected = ReplyResponseCommonDto.builder()
                    .id(2L)
                    .content(filteredContent)
                    .parentId(1L)
                    .postId(targetPost.getId())
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(replyComment.getUser().getId())
                            .name(replyComment.getUser().getName())
                            .email(replyComment.getUser().getEmail())
                            .isAdmin(replyComment.getUser().isAdmin())
                            .isEnabled(replyComment.getUser().isEnabled())
                            .profileImg(replyComment.getUser().getProfileImg())
                            .build())
                    .createdDate(replyComment.getCreatedDate())
                    .lastModifiedDate(replyComment.getLastModifiedDate())
                    .enabled(false)
                    .build();

            assertEquals(1, result.getContent().size());
            assertEquals(expected, result.getContent().get(0));
            verify(commentRepository).findAllByParent(ArgumentMatchers.eq(parentComment), ArgumentMatchers.any(Pageable.class));
            verify(commentRepository).findById(1L);
            verify(userContext).isAdmin();

        }

        @DisplayName("존재하지 않는 부모 댓글 ID를 제공하면, 대댓글 조회에 실패한다.")
        @Test
        void getReply_failWithNotExistsParentComment() {
            // given

            doReturn(Optional.empty()).when(commentRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.getReply(1L, TestDataFactory.getMockPageable()));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(commentRepository).findById(1L);
        }

        @DisplayName("비활성화된 Post ID를 제공하면, 대댓글 조회에 실패한다.")
        @Test
        void getReply_failWithDisabledPost() {
            // given
            var targetPost = createPost(false);

            var parentComment = Comment.builder()
                    .id(1L)
                    .content("parent")
                    .isEnabled(true)
                    .post(targetPost)
                    .user(createUser())
                    .build();


            doReturn(Optional.of(parentComment)).when(commentRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.getReply(1L, TestDataFactory.getMockPageable()));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(commentRepository).findById(1L);

        }

    }

    @DisplayName("commentService.getCommentById")
    @Nested
    class getCommentById {

        private static Stream<Arguments> provideUser() {
            return Stream.of(
                    Arguments.of(createUser(), "일반 유저"),
                    Arguments.of(createAdmin(), "관리자")
            );
        }

        @DisplayName("댓글 ID를 제공하면, 댓글을 반환한다.")
        @MethodSource("provideUser")
        @ParameterizedTest(name = "{index} : {1} 이(가) 댓글 ID를 제공하면, 댓글을 반환한다.")
        void getCommentById_success(User currentUser, String userCase) {
            // given
            var targetPost = createPost(createAddCommentOKPostType());

            var targetComment = Comment.builder()
                    .id(1L)
                    .content("content")
                    .isEnabled(true)
                    .post(targetPost)
                    .user(createUser())
                    .build();

            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);
            doReturn(currentUser.isAdmin()).when(userContext).isAdmin();

            // when
            var result = commentService.getCommentById(1L);

            // then
            var expected = CommentResponseCommonDto.builder()
                    .id(1L)
                    .content("content")
                    .postId(targetPost.getId())
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(targetComment.getUser().getId())
                            .name(targetComment.getUser().getName())
                            .email(targetComment.getUser().getEmail())
                            .isAdmin(targetComment.getUser().isAdmin())
                            .isEnabled(targetComment.getUser().isEnabled())
                            .profileImg(targetComment.getUser().getProfileImg())
                            .build())
                    .createdDate(targetComment.getCreatedDate())
                    .lastModifiedDate(targetComment.getLastModifiedDate())
                    .enabled(true)
                    .build();

            assertEquals(expected, result);
            verify(commentRepository).findById(1L);
            verify(userContext).isAdmin();
        }

        private static Stream<Arguments> provideUserWithFilterContent() {
            return Stream.of(
                    Arguments.of(createUser(), "일반 유저", "삭제된 댓글입니다.", "숨겨진다."),
                    Arguments.of(createAdmin(), "관리자", "content", "숨겨지지 않는다.")
            );
        }

        @DisplayName("비활성화된 댓글이 주어지면, 권한에 따라 댓글 내용이 필터링 된다.")
        @MethodSource("provideUserWithFilterContent")
        @ParameterizedTest(name = "{index} : {1} 이(가) 비활성화된 댓글이 주어지면, {2}")
        void getCommentById_filterWithPermission(User currentUser, String userCase, String filteredContent, String contentCase) {
            // given
            var targetPost = createPost(createAddCommentOKPostType());

            var targetComment = Comment.builder()
                    .id(1L)
                    .content("content")
                    .isEnabled(false)
                    .post(targetPost)
                    .user(createUser())
                    .build();

            doReturn(Optional.of(targetComment)).when(commentRepository).findById(1L);
            doReturn(currentUser.isAdmin()).when(userContext).isAdmin();

            // when
            var result = commentService.getCommentById(1L);

            // then
            var expected = CommentResponseCommonDto.builder()
                    .id(1L)
                    .content(filteredContent)
                    .postId(targetPost.getId())
                    .writer(UserSimpleResponseControllerDto.builder()
                            .id(targetComment.getUser().getId())
                            .name(targetComment.getUser().getName())
                            .email(targetComment.getUser().getEmail())
                            .isAdmin(targetComment.getUser().isAdmin())
                            .isEnabled(targetComment.getUser().isEnabled())
                            .profileImg(targetComment.getUser().getProfileImg())
                            .build())
                    .createdDate(targetComment.getCreatedDate())
                    .lastModifiedDate(targetComment.getLastModifiedDate())
                    .enabled(false)
                    .build();

            assertEquals(expected, result);
            verify(commentRepository).findById(1L);
            verify(userContext).isAdmin();
        }

        @DisplayName("존재하지 않는 댓글 ID를 제공하면, 댓글 조회에 실패한다.")
        @Test
        void getCommentById_failWithNotExistsComment() {
            // given
            doReturn(Optional.empty()).when(commentRepository).findById(1L);

            // when
            var result = assertThrows(BusinessException.class, () -> commentService.getCommentById(1L));

            // then
            assertEquals(BusinessErrorCode.ITEM_NOT_FOUND, result.getErrorCode());
            verify(commentRepository).findById(1L);
        }

    }
}
