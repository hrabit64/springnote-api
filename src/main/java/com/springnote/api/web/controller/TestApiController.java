package com.springnote.api.web.controller;

import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.utils.context.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Profile("test")
@Slf4j
@Validated
@RequestMapping("/test")
@RequiredArgsConstructor
@RestController
public class TestApiController {

    private final UserContext userContext;

    @EnableAuthentication(AuthLevel.NONE)
    @RequestMapping("/auth")
    public String auth() {
        return (userContext.isAuthed()) ? "authed" : "not authed";
    }


}
