package com.springnote.api.utils.exception.validation;

import com.springnote.api.utils.exception.SpringNoteException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ValidationException extends SpringNoteException {
    private String message;
    private ValidationErrorCode errorCode;
}
