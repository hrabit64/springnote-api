package com.springnote.api.dto.post.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.google.gson.annotations.SerializedName;
import com.springnote.api.dto.post.service.PostStatusUpdateRequestServiceDto;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostStatusUpdateRequestControllerDto {

    @SerializedName("enabled")
    @JsonProperty("enabled")
    @NotNull(message = "활성화 여부가 설정되지 않았습니다.")
    private Boolean isEnabled;

    public PostStatusUpdateRequestServiceDto toServiceDto(Long id) {
        return PostStatusUpdateRequestServiceDto.builder()
                .id(id)
                .isEnabled(isEnabled)
                .build();
    }
}
