package com.wcms.auth.application;

public class AuthAccountNotFoundException extends RuntimeException {

    public AuthAccountNotFoundException(String message) {
        super(message);
    }
}
