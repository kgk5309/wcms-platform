package com.wcms.auth.application;

public class DuplicateAuthAccountException extends RuntimeException {

    public DuplicateAuthAccountException(String message) {
        super(message);
    }
}
