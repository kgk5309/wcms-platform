package com.wcms.auth.api;

import com.wcms.core.common.ApiError;
import com.wcms.core.common.ApiResponse;
import com.wcms.core.common.ErrorCode;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AuthExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(new ApiError(ErrorCode.UNAUTHORIZED.name(), exception.getMessage())));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class})
    ResponseEntity<ApiResponse<Void>> handleValidation(Exception exception) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(new ApiError(ErrorCode.INVALID_REQUEST.name(), "Invalid request")));
    }
}
