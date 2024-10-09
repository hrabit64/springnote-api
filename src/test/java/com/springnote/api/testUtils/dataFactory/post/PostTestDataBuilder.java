package com.springnote.api.testUtils.dataFactory.post;

import com.springnote.api.domain.content.Content;
import com.springnote.api.domain.post.Post;
import com.springnote.api.domain.postTag.PostTag;
import com.springnote.api.domain.postType.PostType;
import com.springnote.api.domain.series.Series;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import static com.springnote.api.testUtils.dataFactory.TestDataFactory.testLocalDateTime;
import static com.springnote.api.testUtils.dataFactory.content.ContentTestDataFactory.createContent;
import static com.springnote.api.testUtils.dataFactory.postType.PostTypeTestDataFactory.createPostType;
import static com.springnote.api.testUtils.dataFactory.series.SeriesTestDataFactory.createSeries;

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostTestDataBuilder {

    @Builder.Default
    private Long id = 1L;

    @Builder.Default
    private String title = "title";

    @Builder.Default
    private String thumbnail = "thumbnail";

    @Builder.Default
    private boolean isEnabled = true;

    @Builder.Default
    private Series series = createSeries();

    @Builder.Default
    private PostType postType = createPostType();

    @Builder.Default
    private Content content = createContent();

    private List<PostTag> postTags;

    @Builder.Default
    private LocalDateTime createdDate = testLocalDateTime();

    @Builder.Default
    private LocalDateTime lastModifiedDate = testLocalDateTime();

    public Post toPost() {
        return Post.builder()
                .id(id)
                .title(title)
                .thumbnail(thumbnail)
                .isEnabled(isEnabled)
                .series(series)
                .postType(postType)
                .content(content)
                .postTags(postTags)
                .createdDate(createdDate)
                .lastModifiedDate(lastModifiedDate)
                .build();
    }
}
