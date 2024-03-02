package com.springnote.api.utils.exception.business;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessException extends RuntimeException {
    private String message;
    private BusinessErrorCode errorCode;
}