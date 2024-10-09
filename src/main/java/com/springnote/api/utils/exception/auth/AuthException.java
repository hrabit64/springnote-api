package com.springnote.api.utils.exception.auth;


import com.springnote.api.utils.exception.SpringNoteException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class AuthException extends SpringNoteException {
    private final AuthErrorCode errorCode;
    private final String message;
}
