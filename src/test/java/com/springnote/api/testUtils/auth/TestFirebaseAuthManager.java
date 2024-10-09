package com.springnote.api.testUtils.auth;

import com.springnote.api.security.auth.AuthManager;
import com.springnote.api.security.auth.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class TestFirebaseAuthManager implements AuthManager {


    public AuthUserInfo authenticate(String idToken) {
        try {
            var decodedToken = getWithToken(idToken);
            var uid = decodedToken.getTestUid();
            var displayName = decodedToken.getTestDisplayName();
            return AuthUserInfo.builder()
                    .uid(uid)
                    .displayName(displayName)
                    .email(decodedToken.getTestEmail())
                    .profileImage(decodedToken.getTestProfilePicture())
                    .provider("springnote")
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    private TestFirebaseToken getWithToken(String token) {
        for (var testFirebaseToken : TestFirebaseToken.values()) {
            if (TestFirebaseToken.NOT_VALID.getToken().equalsIgnoreCase(token)) {
                throw new IllegalArgumentException("Invalid token");
            }
            if (testFirebaseToken.getToken().equalsIgnoreCase(token)) {
                return testFirebaseToken;
            }
        }
        throw new IllegalArgumentException("Invalid token");
    }
}
