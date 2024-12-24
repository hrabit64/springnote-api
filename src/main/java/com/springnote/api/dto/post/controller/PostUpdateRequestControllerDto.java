package com.springnote.api.dto.post.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.SerializedName;
import com.springnote.api.dto.general.common.PostTagId;
import com.springnote.api.dto.post.service.PostUpdateRequestServiceDto;
import com.springnote.api.utils.regrex.RegexUtil;
import com.springnote.api.utils.type.DBTypeSize;
import com.springnote.api.utils.validation.list.ListSizeCheck;
import com.springnote.api.utils.validation.list.UniqueItemCheck;
import com.springnote.api.utils.validation.number.NumberRangeCheck;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@EqualsAndHashCode
@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostUpdateRequestControllerDto {

    @NumberRangeCheck(min = 1, max = DBTypeSize.INT, message = "시리즈 ID의 형식이 올바르지 않습니다.", nullable = true)
    private Long seriesId;

    @Valid
    @UniqueItemCheck(message = "태그는 중복될 수 없습니다.")
    @ListSizeCheck(min = 0, max = 10, message = "태그는 최대 10개까지 설정할 수 있습니다.", nullable = true)
    private List<PostTagId> tags = new LinkedList<PostTagId>();

    @Size(min = 3, max = 65535, message = "본문은 3자 이상, 65535자 이하여야 합니다.")
    @NotEmpty(message = "본문을 입력해주세요.")
    private String content;

    @Size(min = 3, max = 300, message = "제목은 3자 이상, 300자 이하여야 합니다.")
    @NotEmpty(message = "제목을 입력해주세요.")
    private String title;

    @Pattern(regexp = RegexUtil.URL_REGEX, message = "썸네일의 주소가 올바르지 않습니다.")
    private String thumbnail;

    @SerializedName("enabled")
    @JsonProperty("enabled")
    @NotNull(message = "활성화 여부가 설정되지 않았습니다.")
    private Boolean isEnabled;

    public PostUpdateRequestServiceDto toServiceDto(Long id) {
        return PostUpdateRequestServiceDto.builder()
                .id(id)
                .seriesId(seriesId)
                .tagIds((tags != null && !tags.isEmpty()) ? tags.stream().map(PostTagId::getId).toList() : List.of())
                .content(content)
                .title(title)
                .thumbnail(thumbnail)
                .isEnabled(isEnabled)
                .build();
    }
}
