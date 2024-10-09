package com.springnote.api.dto.user.controller;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.springnote.api.domain.user.User;
import com.springnote.api.dto.user.common.UserResponseCommonDto;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserSimpleResponseControllerDto {
    private String id;
    private String name;
    private String email;
    private String profileImg;
    private boolean isAdmin;
    private boolean isEnabled;

    public UserSimpleResponseControllerDto(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.profileImg = user.getProfileImg();
        this.isAdmin = user.isAdmin();
        this.isEnabled = user.isEnabled();
    }

    public UserSimpleResponseControllerDto(UserResponseCommonDto userResponseCommonDto) {
        this.id = userResponseCommonDto.getId();
        this.name = userResponseCommonDto.getName();
        this.email = userResponseCommonDto.getEmail();
        this.profileImg = userResponseCommonDto.getProfileImg();
        this.isAdmin = userResponseCommonDto.isAdmin();
        this.isEnabled = userResponseCommonDto.isEnabled();
    }
}