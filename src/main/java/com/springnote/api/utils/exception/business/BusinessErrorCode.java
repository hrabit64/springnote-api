package com.springnote.api.utils.exception.business;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BusinessErrorCode {
    

    ITEM_NOT_FOUND(404, "B_001");
    
    private final Integer statusCode;
    private final String code;
}
