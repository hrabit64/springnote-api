package com.springnote.api.utils.exception.business;

import com.springnote.api.utils.exception.ErrorCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BusinessErrorCode implements ErrorCode {


    ITEM_NOT_FOUND(404, "ItemNotFound"),
    ITEM_ALREADY_EXIST(400, "ItemAlreadyExist"),
    POLICY_VIOLATE(400, "PolicyViolate"),
    ITEM_CONFLICT(409, "ItemConflict"),
    NOT_VALID_ITEM(400, "NotValidItem"),
    SERVER_ERROR(500, "ServerError"),
    FORBIDDEN(403, "Forbidden");

    private final Integer statusCode;
    private final String title;
}
