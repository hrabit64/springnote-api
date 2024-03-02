package com.springnote.api.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.springnote.api.service.PostService;

import lombok.RequiredArgsConstructor;

@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
@RestController
public class PostApiController {
    
    private final PostService postservice;

}
