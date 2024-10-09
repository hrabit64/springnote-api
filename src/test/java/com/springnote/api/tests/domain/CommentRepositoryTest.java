package com.springnote.api.tests.domain;

import com.github.database.rider.core.api.dataset.DataSet;
import com.github.database.rider.core.api.dataset.ExpectedDataSet;
import com.springnote.api.domain.comment.Comment;
import com.springnote.api.domain.comment.CommentRepository;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.user.User;
import com.springnote.api.testUtils.template.RepositoryTestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@DisplayName("Repository Test - Comment")
public class CommentRepositoryTest extends RepositoryTestTemplate {

    @Autowired
    private CommentRepository commentRepository;

    @Nested
    @DisplayName("commentRepository.findAllByPostAndParentIsNull")
    class findAllByPostAndParentIsNull {
        @DisplayName("주어진 포스트의 댓글들을 반환한다..")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/comment-user.yaml",
                "datasets/repository/comment/normal-comment.yaml"
        })
        @Test
        void findAllByPostAndParentIsNull_success() {
            //given
            var postId = 1L;
            var post = Post.builder().id(postId).build();

            //when
            var comments = commentRepository.findAllByPostAndParentIsNull(post, PageRequest.of(0, 10));

            //then
            assertEquals(3, comments.getTotalElements());
            assertTrue(comments.stream().allMatch(comment -> comment.getPost().getId().equals(postId)));
            assertTrue(comments.stream().allMatch(comment -> comment.getParent() == null));
        }

        private static Stream<Arguments> provideSortKey() throws Throwable {
            return Stream.of(

                    Arguments.of("id", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("user", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("user", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("createdDate", Sort.Direction.ASC, List.of(3L, 2L, 1L)),
                    Arguments.of("createdDate", Sort.Direction.DESC, List.of(1L, 2L, 3L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("ip", Sort.Direction.ASC, List.of(1L, 2L, 3L)),
                    Arguments.of("ip", Sort.Direction.DESC, List.of(3L, 2L, 1L)),
                    Arguments.of("isEnabled", Sort.Direction.ASC, List.of(3L, 1L, 2L)),
                    Arguments.of("isEnabled", Sort.Direction.DESC, List.of(1L, 2L, 3L))
            );
        }

        @DisplayName("포스트와 정렬 옵션이 주어지면 해당하는 댓글들을 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/comment-user.yaml",
                "datasets/repository/comment/normal-comment.yaml"
        })
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "{index} : {0} 정렬 옵션이 {1} 방향으로 주어졌을 때, {2} 순으로 정렬된다.")
        void findAllByPostAndParentIsNull_success_with_sort(String sortKey, Sort.Direction direction, List<Long> expected) {
            //given
            var postId = 1L;
            var post = Post.builder().id(postId).build();
            var sort = Sort.by(direction, sortKey);

            //when
            var comments = commentRepository.findAllByPostAndParentIsNull(post, PageRequest.of(0, 10, sort));

            //then
            assertEquals(3, comments.getTotalElements());
            assertEquals(expected, comments.map(Comment::getId).getContent());
        }

    }

    @Nested
    @DisplayName("commentRepository.findAllByParent")
    class findAllByParent {
        @DisplayName("주어진 부모 댓글에 해당하는 댓글들을 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/comment-user.yaml",
                "datasets/repository/comment/reply-comment.yaml"
        })
        @Test
        void findAllByParent_success() {
            //given
            var parentCommentId = 1L;

            var parent = Comment.builder().id(parentCommentId).build();
            //when
            var comments = commentRepository.findAllByParent(parent, PageRequest.of(0, 10));

            //then
            assertEquals(3, comments.getTotalElements());
            assertTrue(comments.stream().allMatch(comment -> comment.getParent().getId().equals(parentCommentId)));
        }

        private static Stream<Arguments> provideSortKey() throws Throwable {
            return Stream.of(

                    Arguments.of("id", Sort.Direction.ASC, List.of(3L, 4L, 5L)),
                    Arguments.of("id", Sort.Direction.DESC, List.of(5L, 4L, 3L)),
                    Arguments.of("user", Sort.Direction.ASC, List.of(3L, 4L, 5L)),
                    Arguments.of("user", Sort.Direction.DESC, List.of(5L, 4L, 3L)),
                    Arguments.of("createdDate", Sort.Direction.ASC, List.of(5L, 4L, 3L)),
                    Arguments.of("createdDate", Sort.Direction.DESC, List.of(3L, 4L, 5L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.ASC, List.of(3L, 4L, 5L)),
                    Arguments.of("lastModifiedDate", Sort.Direction.DESC, List.of(5L, 4L, 3L)),
                    Arguments.of("ip", Sort.Direction.ASC, List.of(3L, 4L, 5L)),
                    Arguments.of("ip", Sort.Direction.DESC, List.of(5L, 4L, 3L)),
                    Arguments.of("isEnabled", Sort.Direction.ASC, List.of(5L, 3L, 4L)),
                    Arguments.of("isEnabled", Sort.Direction.DESC, List.of(3L, 4L, 5L))
            );
        }

        @DisplayName("부모 댓글과 정렬 옵션이 주어지면 해당하는 댓글들을 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/comment-user.yaml",
                "datasets/repository/comment/reply-comment.yaml"
        })
        @MethodSource("provideSortKey")
        @ParameterizedTest(name = "{0} 정렬 옵션이 {1} 방향으로 주어졌을 때, {2} 순으로 정렬된다.")
        void findAllByParent_success_with_sort(String sortKey, Sort.Direction direction, List<Long> expected) {
            //given
            var parentCommentId = 1L;

            var parent = Comment.builder().id(parentCommentId).build();
            var sort = Sort.by(direction, sortKey);

            //when
            var comments = commentRepository.findAllByParent(parent, PageRequest.of(0, 10, sort));

            //then
            assertEquals(3, comments.getTotalElements());
            assertEquals(expected, comments.map(Comment::getId).getContent());
        }
    }

    @Nested
    @DisplayName("commentRepository.countReplyByParentIds")
    class countReplyByParentIds {

        @DisplayName("대댓글이 존재하는 부모가 주어지면 대댓글 수를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/comment-user.yaml",
                "datasets/repository/comment/cnt-reply-comment.yaml"
        })
        @Test
        void countReplyByParentIds_success() {
            //given
            var hasReplyParentId = 1L;
            var parentIds = Set.of(hasReplyParentId);

            //when
            var replyCount = commentRepository.countReplyByParentIds(parentIds);

            //then
            assertEquals(1, replyCount.size());
            assertEquals(3, replyCount.get(0).getReplyCount());
        }

        @DisplayName("대댓글이 존재하지 않는  부모가 주어지면 빈 리스트를 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/comment-user.yaml",
                "datasets/repository/comment/cnt-reply-comment.yaml"
        })
        @Test
        void countReplyByParentIds_fail() {
            //given
            var notHasReplyParentId = 2L;
            var parentIds = Set.of(notHasReplyParentId);

            //when
            var replyCount = commentRepository.countReplyByParentIds(parentIds);

            //then
            assertEquals(0, replyCount.size());
        }

    }

    @Nested
    @DisplayName("commentRepository.findById")
    class findById {
        @DisplayName("주어진 id에 해당하는 댓글이 존재하면, 해당 댓글을 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/base-user.yaml",
                "datasets/repository/comment/base-comment.yaml"
        })
        @Test
        void findById_success() {
            //given
            var validId = 1L;

            //when
            var comment = commentRepository.findById(validId).orElse(null);

            //then
            assertNotNull(comment);
            assertEquals(validId, comment.getId());
        }

        @DisplayName("주어진 id에 해당하는 댓글이 존재하지 않으면, 빈 Optional을 반환한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/base-user.yaml",
                "datasets/repository/comment/base-comment.yaml"
        })
        @Test
        void findById_fail() {
            //given
            var invalidId = 0L;

            //when
            var comment = commentRepository.findById(invalidId).orElse(null);

            //then
            assertNull(comment);
        }
    }

    @Nested
    @DisplayName("commentRepository.save")
    class save {

        @DisplayName("올바른 댓글이 주어지면 저장한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/comment-user.yaml",
                "datasets/repository/comment/empty-comment.yaml"
        })
        @ExpectedDataSet(value = "datasets/repository/comment/saved-comment.yaml")
        @Test
        void save_success() {
            //given
            var comment = Comment.builder()
                    .content("This is a test comment.")
                    .isEnabled(true)
                    .ip("1.1.1.1")
                    .post(Post.builder().id(1L).build())
                    .user(User.builder().id("1-this-is-a-test-user-uid!").build())
                    .build();

            //when
            commentRepository.save(comment);
        }

        @DisplayName("올바른 대댓글이 주어지면 저장한다.")
        @DataSet(value = {
                "datasets/repository/postType/base-postType.yaml",
                "datasets/repository/series/base-series.yaml",
                "datasets/repository/content/base-content.yaml",
                "datasets/repository/post/base-post.yaml",
                "datasets/repository/user/comment-user.yaml",
                "datasets/repository/comment/reply-save-before-comment.yaml"
        })
        @ExpectedDataSet(value = "datasets/repository/comment/saved-reply-comment.yaml")
        @Test
        void save_successReply() {
            //given
            var comment = Comment.builder()
                    .content("This is a test comment.")
                    .isEnabled(true)
                    .ip("1.1.1.1")
                    .post(Post.builder().id(1L).build())
                    .user(User.builder().id("1-this-is-a-test-user-uid!").build())
                    .parent(Comment.builder().id(1L).build())
                    .build();

            //when
            commentRepository.save(comment);
        }
    }
}
