package com.springnote.api.security.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthUserInfo {
    private String uid;
    private String displayName;
    private String profileImage;
    private String email;
    private String provider;
}
