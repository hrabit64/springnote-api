package com.springnote.api.dto.tmpPost.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.dto.general.common.PostTagId;
import com.springnote.api.dto.tmpPost.service.TmpPostUpdateRequestServiceDto;
import com.springnote.api.utils.regrex.RegexUtil;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.validation.list.ListSizeCheck;
import com.springnote.api.utils.validation.number.NumberRangeCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TmpPostUpdateRequestControllerDto {

    @NumberRangeCheck(min = 1, max = DBTypeSize.INT, message = "시리즈 ID의 형식이 올바르지 않습니다.", nullable = true)
    private Long seriesId;

    @Valid
    @ListSizeCheck(min = 0, max = 10, message = "태그는 최대 10개까지 설정할 수 있습니다.", nullable = true)
    private List<PostTagId> tagIds;

    @Size(max = 65535, message = "본문은 65535자 이하여야 합니다.")
    private String content;

    @Size(max = 300, message = "제목은 300자 이하여야 합니다.")
    private String title;

    @Pattern(regexp = RegexUtil.URL_REGEX, message = "썸네일의 주소가 올바르지 않습니다.")
    private String thumbnail;

    public TmpPostUpdateRequestServiceDto toServiceDto(String id) {
        return TmpPostUpdateRequestServiceDto.builder()
                .id(id)
                .seriesId(seriesId)
                .tagIds((tagIds != null && !tagIds.isEmpty()) ? tagIds.stream().map(PostTagId::getId).toList() : List.of())
                .content(content)
                .title(title)
                .thumbnail(thumbnail)
                .build();
    }
}
