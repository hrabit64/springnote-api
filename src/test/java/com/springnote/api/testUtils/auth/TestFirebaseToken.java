package com.springnote.api.testUtils.auth;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TestFirebaseToken {
    NOT_VALID("not_valid_token", null, null, null, null),
    USER_TOKEN("user_token",
            "this_is_user_uid_123456789",
            "user",
            "user@springnote.blog",
            "https://springnote.blog/profile/this-is-a-test-user-uid123"),
    ADMIN_TOKEN("admin_token",
            "this_is_admin_uid_12345678",
            "admin",
            "admin@springnote.blog",
            "https://springnote.blog/profile/this-is-a-test-user-uid123"
    ),
    DISABLED_USER_TOKEN("disabled_user_token",
            "this_is_disabled_user_uid_",
            "disabled_user",
            "disabled_user@springnote.blog",
            "https://springnote.blog/profile/this-is-a-test-user-uid123"
    ),
    DISABLED_ADMIN_TOKEN("disabled_admin_token",
            "this_is_disabled_admin_uid",
            "disabled_admin",
            "disabled_admin@springnote.blog",
            "https://springnote.blog/profile/this-is-a-test-user-uid123"
    );


    private final String token;
    private final String testUid;
    private final String testDisplayName;
    private final String testEmail;
    private final String testProfilePicture;
}
