package com.springnote.api.utils.exception.validation;

import com.springnote.api.utils.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ValidationErrorCode implements ErrorCode {


    BAD_ARGS(400, "BadArgs"),
    BAD_PERMISSION(403, "BadPermission"),
    HONEYPOT(403, "Honeypot!"),
    ;

    private final Integer statusCode;
    private final String title;
}
