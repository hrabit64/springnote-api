package com.springnote.api.dto.user.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.user.User;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserResponseCommonDto {
    private String id;
    private String name;
    private String email;
    private String provider;
    private String profileImg;
    private boolean isAdmin;
    private boolean isEnabled;

    public UserResponseCommonDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.provider = user.getProvider();
        this.profileImg = user.getProfileImg();
        this.isAdmin = user.isAdmin();
        this.isEnabled = user.isEnabled();
    }
}
