package com.wcms.core.common;

public class WcmsException extends RuntimeException {

    private final ErrorCode errorCode;

    public WcmsException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
