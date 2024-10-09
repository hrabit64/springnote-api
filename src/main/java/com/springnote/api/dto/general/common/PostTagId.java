package com.springnote.api.dto.general.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.utils.type.DBTypeSize;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PostTagId {

    @Max(value = DBTypeSize.INT, message = "시리즈 ID의 형식이 올바르지 않습니다.")
    @Min(value = 1, message = "시리즈 ID의 형식이 올바르지 않습니다.")
    private Long id;
}
