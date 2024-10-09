package com.springnote.api.web.controller;


import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RequestMapping("")
@RequiredArgsConstructor
@RestController
public class HelloController {

    @GetMapping("/")
    public String hello() {
        return "Hello, SpringNote!";
    }

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/teapot")
    public ResponseEntity<String> teapot() {
        return ResponseEntity.status(418).body("I'm a teapot");
    }

    @EnableAuthentication(AuthLevel.NONE)
    @GetMapping("/none")
    public String helloNone() {
        return "Hello, SpringNote!";
    }

    @EnableAuthentication(AuthLevel.ADMIN)
    @GetMapping("/admin")
    public String helloAdmin() {
        return "Hello, SpringNote Admin!";
    }

    @EnableAuthentication(AuthLevel.USER)
    @GetMapping("/user")
    public String helloUser() {
        return "Hello, SpringNote User!";
    }

}
