package com.springnote.api.security.auth;

import com.google.firebase.auth.FirebaseAuth;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Profile("!test")
@RequiredArgsConstructor
@Component
public class FirebaseAuthManager implements AuthManager {
    private final FirebaseAuth firebaseAuth;

    public AuthUserInfo authenticate(String idToken) {
        try {
            var decodedToken = firebaseAuth.verifyIdToken(idToken);
            var uid = decodedToken.getUid();
            var displayName = decodedToken.getName();
            var profileImage = decodedToken.getPicture();
            var email = decodedToken.getEmail();

            return AuthUserInfo.builder()
                    .uid(uid)
                    .displayName(displayName)
                    .profileImage(profileImage)
                    .email(email)
                    .provider("firebase")
                    .build();

        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public boolean deactive(String id) {
        try {
            firebaseAuth.deleteUser(id);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
