package com.springnote.api.config;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestFirebaseConfig {

    @Bean
    public FirebaseApp firebaseApp() {
        return mock(FirebaseApp.class);
    }

    @Bean
    public FirebaseAuth firebaseAuth() {
        return mock(FirebaseAuth.class);
    }
}
