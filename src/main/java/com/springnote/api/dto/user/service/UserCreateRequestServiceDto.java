package com.springnote.api.dto.user.service;

import com.springnote.api.domain.user.User;
import com.springnote.api.security.auth.AuthUserInfo;
import lombok.*;

@EqualsAndHashCode
@ToString
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
public class UserCreateRequestServiceDto {
    private String name;
    private String uid;
    private String profileImage;
    private String email;
    private String provider;

    public UserCreateRequestServiceDto(AuthUserInfo authUserInfo) {
        this.name = authUserInfo.getDisplayName();
        this.uid = authUserInfo.getUid();
        this.profileImage = authUserInfo.getProfileImage();
        this.email = authUserInfo.getEmail();
        this.provider = authUserInfo.getProvider();
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .id(uid)
                .profileImg(profileImage)
                .email(email)
                .provider(provider)
                .isAdmin(false)
                .isEnabled(true)
                .build();
    }

}
