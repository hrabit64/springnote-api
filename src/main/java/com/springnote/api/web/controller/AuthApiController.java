package com.springnote.api.web.controller;


import com.springnote.api.aop.auth.AuthLevel;
import com.springnote.api.aop.auth.EnableAuthentication;
import com.springnote.api.config.AuthConfig;
import com.springnote.api.dto.user.common.UserSimpleResponseCommonDto;
import com.springnote.api.dto.user.service.UserCreateRequestServiceDto;
import com.springnote.api.security.auth.AuthManager;
import com.springnote.api.security.auth.TokenHeaderUtils;
import com.springnote.api.service.UserService;
import com.springnote.api.utils.context.UserContext;
import com.springnote.api.utils.validation.bot.CheckCaptcha;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Validated
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@RestController
public class AuthApiController {
    private final UserService userService;
    private final UserContext userContext;
    private final AuthManager authManager;
    private final AuthConfig authConfig;


    @EnableAuthentication(AuthLevel.NONE)
    @PostMapping
    public ResponseEntity<Void> register(
            @NotEmpty
            @CheckCaptcha
            @RequestParam(name = "captchaToken", required = true)
            String captchaToken,

            @RequestHeader("Authorization")
            String authorization
    ) {
        if (userContext.isAuthed()) {
            return ResponseEntity.badRequest().build();
        }

        var token = TokenHeaderUtils.extractToken(authorization);

        if (token == null) {
            return ResponseEntity.status(403).build();
        }

        var userInfo = authManager.authenticate(token);

        if (userInfo == null) {
            return ResponseEntity.status(403).build();
        }

        var request = new UserCreateRequestServiceDto(userInfo);
        validateUserInfo(request);
        userService.register(request);

        return ResponseEntity.ok().build();
    }

    public void validateUserInfo(UserCreateRequestServiceDto user) {
        if (user.getProfileImage() == null || user.getProfileImage().isEmpty()) {
            user.setProfileImage(authConfig.getDefaultProfileImg());
        }

        if (user.getEmail() == null || user.getEmail().isEmpty()) {
            throw new IllegalArgumentException("유저 생성 시도중 받은 email이 비어있습니다.");
        }

        if (user.getName() == null || user.getName().isEmpty()) {
            throw new IllegalArgumentException("유저 생성 시도중 받은 name이 비어있습니다.");
        }
    }

    @EnableAuthentication(AuthLevel.USER)
    @GetMapping
    public ResponseEntity<UserSimpleResponseCommonDto> getSelfInfo() {

        return ResponseEntity.ok(
                userContext.toDto()
        );

    }

    @EnableAuthentication(AuthLevel.USER)
    @DeleteMapping
    public ResponseEntity<Void> deleteSelf(
            @NotEmpty
            @CheckCaptcha
            @RequestParam(name = "captchaToken", required = true)
            String captchaToken
    ) {
        userService.deleteUser(userContext.getUid());
        return ResponseEntity.ok().build();
    }
}
