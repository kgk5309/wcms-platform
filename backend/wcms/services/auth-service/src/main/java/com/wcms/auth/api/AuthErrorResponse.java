package com.wcms.auth.api;

public record AuthErrorResponse(
        boolean succeeded,
        ErrorBody error
) {

    public static AuthErrorResponse failure(String code, String message) {
        return new AuthErrorResponse(false, new ErrorBody(code, message));
    }

    public record ErrorBody(
            String code,
            String message
    ) {
    }
}
