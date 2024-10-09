package com.springnote.api.testUtils.dataFactory.user;

import com.springnote.api.domain.user.User;
import com.springnote.api.testUtils.RandomStringGenerator;

public class UserTestDataFactory {
    public static User createUser(boolean isEnable) {
        return User.builder()
                .id(RandomStringGenerator.generateRandomString(26))
                .name("testname")
                .email("test@springnote.blog")
                .isAdmin(false)
                .provider("springnote")
                .profileImg("springnote.blog")
                .isEnabled(isEnable)
                .build();
    }

    public static User createUser() {
        return User.builder()
                .id(RandomStringGenerator.generateRandomString(26))
                .name("testname")
                .email("test@springnote.blog")
                .isAdmin(false)
                .provider("springnote")
                .profileImg("springnote.blog")
                .isEnabled(true)
                .build();
    }

    public static User createAdmin(boolean isEnable) {
        return User.builder()
                .id(RandomStringGenerator.generateRandomString(26))
                .name("testname")
                .email("test@springnote.blog")
                .isAdmin(true)
                .provider("springnote")
                .profileImg("springnote.blog")
                .isEnabled(isEnable)
                .build();
    }

    public static User createAdmin() {
        return User.builder()
                .id(RandomStringGenerator.generateRandomString(26))
                .name("testname")
                .email("test@springnote.blog")
                .isAdmin(true)
                .provider("springnote")
                .profileImg("springnote.blog")
                .isEnabled(true)
                .build();
    }

}
