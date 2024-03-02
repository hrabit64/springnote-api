package com.springnote.api.utils.exception.general;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationException extends RuntimeException {
    private String message;
}

