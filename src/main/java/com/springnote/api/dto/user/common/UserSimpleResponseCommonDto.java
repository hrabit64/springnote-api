package com.springnote.api.dto.user.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserSimpleResponseCommonDto {
    private String uid;
    private String displayName;
    private String profileImg;
    private boolean isAdmin;
    private boolean isEnabled;
}
