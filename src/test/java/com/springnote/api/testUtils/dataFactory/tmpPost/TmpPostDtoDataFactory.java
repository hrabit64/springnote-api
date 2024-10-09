package com.springnote.api.testUtils.dataFactory.tmpPost;

import com.springnote.api.dto.postType.common.PostTypeResponseDto;
import com.springnote.api.dto.series.common.SeriesSimpleResponseDto;
import com.springnote.api.dto.tag.common.TagResponseDto;
import com.springnote.api.dto.tmpPost.common.TmpPostResponseCommonDto;
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class TmpPostDtoDataFactory {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");


    public static TmpPostResponseCommonDto createTmpPostResponseCommonDto() {
        return TmpPostResponseCommonDto.builder()
                .id(UUID.randomUUID().toString())
                .title("title")
                .content("content")
                .lastUpdatedAt(testLocalDateTime())
                .createdAt(testLocalDateTime())
                .tags(
                        List.of(
                                new TagResponseDto(1L, "tag1"),
                                new TagResponseDto(2L, "tag2")
                        )
                )
                .postType(
                        new PostTypeResponseDto(1L, "postType")
                )
                .series(
                        new SeriesSimpleResponseDto(1L, "series")
                )
                .thumbnail("http://springnote.blog")
                .build();
    }

    public static void createMatcher(TmpPostResponseCommonDto expected, ResultActions result, boolean isPageable) throws Exception {
        if (isPageable) {
            result.andExpect(jsonPath("$._embedded.tmp_posts[0].id").value(expected.getId()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].title").value(expected.getTitle()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].content").value(expected.getContent()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].last_updated_at").value(expected.getLastUpdatedAt().format(formatter)))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].created_at").value(expected.getCreatedAt().format(formatter)))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].tags[0].id").value(expected.getTags().get(0).getId()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].tags[0].name").value(expected.getTags().get(0).getName()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].tags[1].id").value(expected.getTags().get(1).getId()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].tags[1].name").value(expected.getTags().get(1).getName()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].post_type.id").value(expected.getPostType().getId()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].post_type.name").value(expected.getPostType().getName()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].series.id").value(expected.getSeries().getId()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].series.name").value(expected.getSeries().getName()))
                    .andExpect(jsonPath("$._embedded.tmp_posts[0].thumbnail").value(expected.getThumbnail()));
        } else {
            result.andExpect(jsonPath("$.id").value(expected.getId()))
                    .andExpect(jsonPath("$.title").value(expected.getTitle()))
                    .andExpect(jsonPath("$.content").value(expected.getContent()))
                    .andExpect(jsonPath("$.last_updated_at").value(expected.getLastUpdatedAt().format(formatter)))
                    .andExpect(jsonPath("$.created_at").value(expected.getCreatedAt().format(formatter)))
                    .andExpect(jsonPath("$.tags[0].id").value(expected.getTags().get(0).getId()))
                    .andExpect(jsonPath("$.tags[0].name").value(expected.getTags().get(0).getName()))
                    .andExpect(jsonPath("$.tags[1].id").value(expected.getTags().get(1).getId()))
                    .andExpect(jsonPath("$.tags[1].name").value(expected.getTags().get(1).getName()))
                    .andExpect(jsonPath("$.post_type.id").value(expected.getPostType().getId()))
                    .andExpect(jsonPath("$.post_type.name").value(expected.getPostType().getName()))
                    .andExpect(jsonPath("$.series.id").value(expected.getSeries().getId()))
                    .andExpect(jsonPath("$.series.name").value(expected.getSeries().getName()))
                    .andExpect(jsonPath("$.thumbnail").value(expected.getThumbnail()));
        }
    }
}
