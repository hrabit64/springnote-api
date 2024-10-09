package com.springnote.api.utils.exception.business;

import com.springnote.api.utils.exception.SpringNoteException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BusinessException extends SpringNoteException {
    private String message;
    private BusinessErrorCode errorCode;
}