package com.springnote.api.utils.exception;

public abstract class SpringNoteException extends RuntimeException {
    public abstract ErrorCode getErrorCode();
}
