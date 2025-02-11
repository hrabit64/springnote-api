package com.springnote.api.security.auth;

public interface AuthManager {
    AuthUserInfo authenticate(String idToken);
    boolean deactive(String id);
}
