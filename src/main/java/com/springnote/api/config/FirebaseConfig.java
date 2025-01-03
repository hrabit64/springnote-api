package com.springnote.api.config;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Profile("!test")
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${springnote.firebase.config.name}")
    private String firebaseConfigName;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        log.info("Initializing Firebase.");

        var options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(new ClassPathResource(firebaseConfigName).getInputStream()))
                .build();
        FirebaseApp app;
        if (FirebaseApp.getApps().isEmpty()) {
            app = FirebaseApp.initializeApp(options);
        } else {
            app = FirebaseApp.getApps().get(0);
        }

        log.info("FirebaseApp initialized {}", app.getName());

        return app;
    }

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        return FirebaseAuth.getInstance(firebaseApp());
    }


}
