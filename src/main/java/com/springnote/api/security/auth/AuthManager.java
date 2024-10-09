package com.springnote.api.security.auth;

public interface AuthManager {
    AuthUserInfo authenticate(String idToken);
}
