package com.springnote.api.testUtils.dataFactory.comment;

import com.springnote.api.dto.comment.common.CommentResponseCommonDto;
import com.springnote.api.dto.comment.common.CommentResponseWithReplyCntCommonDto;
import com.springnote.api.dto.comment.common.ReplyResponseCommonDto;
import com.springnote.api.dto.user.controller.UserSimpleResponseControllerDto;
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class CommentDtoTestDataFactory {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public static CommentResponseCommonDto createCommentResponseCommonDto() {
        return CommentResponseCommonDto.builder()
                .id(1L)
                .content("test")
                .createdDate(testLocalDateTime())
                .lastModifiedDate(testLocalDateTime())
                .postId(1L)
                .writer(UserSimpleResponseControllerDto.builder()
                        .id("test")
                        .name("test")
                        .profileImg("https://springnote.blog")
                        .email("admin@springnote.blog")
                        .isAdmin(true)
                        .isEnabled(true)
                        .build())
                .build();
    }

    public static ReplyResponseCommonDto createReplyResponseCommonDto() {
        return ReplyResponseCommonDto.builder()
                .id(1L)
                .content("test")
                .createdDate(testLocalDateTime())
                .lastModifiedDate(testLocalDateTime())
                .postId(1L)
                .parentId(1L)
                .writer(UserSimpleResponseControllerDto.builder()
                        .id("test")
                        .name("test")
                        .profileImg("https://springnote.blog")
                        .email("admin@springnote.blog")
                        .isAdmin(true)
                        .isEnabled(true)
                        .build())
                .build();
    }

    public static CommentResponseWithReplyCntCommonDto createCommentResponseWithReplyCntCommonDto() {
        return CommentResponseWithReplyCntCommonDto.builder()
                .id(1L)
                .content("test")
                .createdDate(testLocalDateTime())
                .lastModifiedDate(testLocalDateTime())
                .postId(1L)
                .replyCount(1L)
                .writer(UserSimpleResponseControllerDto.builder()
                        .id("test")
                        .name("test")
                        .profileImg("https://springnote.blog")
                        .email("admin@springnote.blog")
                        .isAdmin(true)
                        .isEnabled(true)
                        .build())
                .build();
    }

    public static void createMatcher(CommentResponseWithReplyCntCommonDto expected, ResultActions result, boolean isPageable) throws Exception {
        if (isPageable) {
            result.andExpect(jsonPath("$._embedded.comments").isArray())
                    .andExpect(jsonPath("$._embedded.comments[0].id").value(expected.getId()))
                    .andExpect(jsonPath("$._embedded.comments[0].post_id").value(expected.getPostId()))
                    .andExpect(jsonPath("$._embedded.comments[0].created_date").value(expected.getCreatedDate().format(formatter)))
                    .andExpect(jsonPath("$._embedded.comments[0].last_modified_date").value(expected.getLastModifiedDate().format(formatter)))
                    .andExpect(jsonPath("$._embedded.comments[0].enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$._embedded.comments[0].content").value(expected.getContent()))
                    .andExpect(jsonPath("$._embedded.comments[0].reply_count").value(expected.getReplyCount()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.id").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.name").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.profile_img").value(expected.getWriter().getProfileImg()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.email").value(expected.getWriter().getEmail()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.admin").value(expected.getWriter().isAdmin()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.enabled").value(expected.getWriter().isEnabled()));
        } else {
            result.andExpect(jsonPath("$.id").value(expected.getId()))
                    .andExpect(jsonPath("$.post_id").value(expected.getContent()))
                    .andExpect(jsonPath("$.created_date").value(expected.getCreatedDate().format(formatter)))
                    .andExpect(jsonPath("$.last_modified_date").value(expected.getLastModifiedDate().format(formatter)))
                    .andExpect(jsonPath("$.enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$.content").value(expected.getContent()))
                    .andExpect(jsonPath("$.reply_count").value(expected.getReplyCount()))
                    .andExpect(jsonPath("$.writer.id").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$.writer.name").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$.writer.profile_img").value(expected.getWriter().getProfileImg()))
                    .andExpect(jsonPath("$.writer.email").value(expected.getWriter().getEmail()))
                    .andExpect(jsonPath("$.writer.admin").value(expected.getWriter().isAdmin()))
                    .andExpect(jsonPath("$.writer.enabled").value(expected.getWriter().isEnabled()));
        }
    }

    public static void createMatcher(ReplyResponseCommonDto expected, ResultActions result, boolean isPageable) throws Exception {
        if (isPageable) {
            result.andExpect(jsonPath("$._embedded.replies").isArray())
                    .andExpect(jsonPath("$._embedded.replies[0].id").value(expected.getId()))
                    .andExpect(jsonPath("$._embedded.replies[0].post_id").value(expected.getPostId()))
                    .andExpect(jsonPath("$._embedded.replies[0].parent_id").value(expected.getParentId()))
                    .andExpect(jsonPath("$._embedded.replies[0].created_date").value(expected.getCreatedDate().format(formatter)))
                    .andExpect(jsonPath("$._embedded.replies[0].last_modified_date").value(expected.getLastModifiedDate().format(formatter)))
                    .andExpect(jsonPath("$._embedded.replies[0].enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$._embedded.replies[0].content").value(expected.getContent()))
                    .andExpect(jsonPath("$._embedded.replies[0].writer.id").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$._embedded.replies[0].writer.name").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$._embedded.replies[0].writer.profile_img").value(expected.getWriter().getProfileImg()))
                    .andExpect(jsonPath("$._embedded.replies[0].writer.email").value(expected.getWriter().getEmail()))
                    .andExpect(jsonPath("$._embedded.replies[0].writer.admin").value(expected.getWriter().isAdmin()))
                    .andExpect(jsonPath("$._embedded.replies[0].writer.enabled").value(expected.getWriter().isEnabled()));
        } else {
            result.andExpect(jsonPath("$.id").value(expected.getId()))
                    .andExpect(jsonPath("$.post_id").value(expected.getPostId()))
                    .andExpect(jsonPath("$.parent_id").value(expected.getParentId()))
                    .andExpect(jsonPath("$.created_date").value(expected.getCreatedDate().format(formatter)))
                    .andExpect(jsonPath("$.last_modified_date").value(expected.getLastModifiedDate().format(formatter)))
                    .andExpect(jsonPath("$.enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$.content").value(expected.getContent()))
                    .andExpect(jsonPath("$.writer.id").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$.writer.name").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$.writer.profile_img").value(expected.getWriter().getProfileImg()))
                    .andExpect(jsonPath("$.writer.email").value(expected.getWriter().getEmail()))
                    .andExpect(jsonPath("$.writer.admin").value(expected.getWriter().isAdmin()))
                    .andExpect(jsonPath("$.writer.enabled").value(expected.getWriter().isEnabled()));
        }

    }

    public static void createMatcher(CommentResponseCommonDto expected, ResultActions result, boolean isPageable) throws Exception {
        if (isPageable) {
            result.andExpect(jsonPath("$._embedded.comments").isArray())
                    .andExpect(jsonPath("$._embedded.comments[0].id").value(expected.getId()))
                    .andExpect(jsonPath("$._embedded.comments[0].post_id").value(expected.getPostId()))
                    .andExpect(jsonPath("$._embedded.comments[0].created_date").value(expected.getCreatedDate().format(formatter)))
                    .andExpect(jsonPath("$._embedded.comments[0].last_modified_date").value(expected.getLastModifiedDate().format(formatter)))
                    .andExpect(jsonPath("$._embedded.comments[0].enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$._embedded.comments[0].content").value(expected.getContent()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.id").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.name").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.profile_img").value(expected.getWriter().getProfileImg()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.email").value(expected.getWriter().getEmail()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.admin").value(expected.getWriter().isAdmin()))
                    .andExpect(jsonPath("$._embedded.comments[0].writer.enabled").value(expected.getWriter().isEnabled()));
        } else {
            result.andExpect(jsonPath("$.id").value(expected.getId()))
                    .andExpect(jsonPath("$.post_id").value(expected.getPostId()))
                    .andExpect(jsonPath("$.created_date").value(expected.getCreatedDate().format(formatter)))
                    .andExpect(jsonPath("$.last_modified_date").value(expected.getLastModifiedDate().format(formatter)))
                    .andExpect(jsonPath("$.enabled").value(expected.isEnabled()))
                    .andExpect(jsonPath("$.content").value(expected.getContent()))
                    .andExpect(jsonPath("$.writer.id").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$.writer.name").value(expected.getWriter().getName()))
                    .andExpect(jsonPath("$.writer.profile_img").value(expected.getWriter().getProfileImg()))
                    .andExpect(jsonPath("$.writer.email").value(expected.getWriter().getEmail()))
                    .andExpect(jsonPath("$.writer.admin").value(expected.getWriter().isAdmin()))
                    .andExpect(jsonPath("$.writer.enabled").value(expected.getWriter().isEnabled()));
        }
    }
}
