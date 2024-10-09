package com.springnote.api.dto.tmpPost.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.dto.general.common.PostTagId;
import com.springnote.api.dto.tmpPost.service.TmpPostCreateRequestServiceDto;
import com.springnote.api.utils.regrex.RegexUtil;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.validation.list.ListSizeCheck;
import com.springnote.api.utils.validation.list.UniqueItemCheck;
import com.springnote.api.utils.validation.number.NumberRangeCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.hateoas.server.core.Relation;

import java.util.List;

@Relation(collectionRelation = "tmpPosts")
@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TmpPostCreateRequestControllerDto {

    @NumberRangeCheck(min = 1, max = DBTypeSize.INT, message = "시리즈 ID의 형식이 올바르지 않습니다.", nullable = true)
    private Long seriesId;

    @Valid
    @UniqueItemCheck(message = "태그는 중복될 수 없습니다.")
    @ListSizeCheck(min = 0, max = 10, message = "태그는 최대 10개까지 설정할 수 있습니다.", nullable = true)
    private List<PostTagId> tagIds;

    @Size(max = 30000, message = "본문은 30000자 이하여야 합니다.")
    private String content;

    @Size(max = 300, message = "제목은 300자 이하여야 합니다.")
    private String title;

    @Pattern(regexp = RegexUtil.URL_REGEX, message = "썸네일의 주소가 올바르지 않습니다.")
    private String thumbnail;

    @Max(value = DBTypeSize.TINYINT, message = "게시글 유형 ID의 형식이 올바르지 않습니다.")
    @Min(value = 1, message = "게시글 유형 ID의 형식이 올바르지 않습니다.")
    @NotNull(message = "게시글 유형 ID는 필수입니다.")
    private Long postTypeId;

    public TmpPostCreateRequestServiceDto toServiceDto() {
        return TmpPostCreateRequestServiceDto.builder()
                .seriesId(seriesId)
                .tagIds((tagIds != null && !tagIds.isEmpty()) ? tagIds.stream().map(PostTagId::getId).toList() : List.of())
                .content(content)
                .title(title)
                .thumbnail(thumbnail)
                .postTypeId(postTypeId)
                .build();
    }
}
