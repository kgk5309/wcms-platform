package com.wcms.user.application;

public class DuplicateUserProfileException extends RuntimeException {

    public DuplicateUserProfileException(String message) {
        super(message);
    }
}
