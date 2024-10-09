package com.springnote.api.utils.exception.auth;

import com.springnote.api.utils.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AuthErrorCode implements ErrorCode {

    FORBIDDEN_ROLE(403, "ForbiddenRole"),
    INVALID_CREDENTIALS(403, "InvalidCredentials"),
    ;

    private final Integer statusCode;
    private final String title;
}
