package com.wcms.user.api;

import com.wcms.core.common.ApiError;
import com.wcms.core.common.ApiResponse;
import com.wcms.core.common.ErrorCode;
import com.wcms.user.application.DuplicateUserProfileException;
import com.wcms.user.application.UserProfileNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(DuplicateUserProfileException.class)
    ResponseEntity<ApiResponse<Void>> handleDuplicate(DuplicateUserProfileException exception) {
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.failure(new ApiError(ErrorCode.CONFLICT.name(), exception.getMessage())));
    }

    @ExceptionHandler(UserProfileNotFoundException.class)
    ResponseEntity<ApiResponse<Void>> handleNotFound(UserProfileNotFoundException exception) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.failure(new ApiError(ErrorCode.NOT_FOUND.name(), exception.getMessage())));
    }

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ApiResponse<Void>> handleBadCredentials(BadCredentialsException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.failure(new ApiError(ErrorCode.UNAUTHORIZED.name(), exception.getMessage())));
    }

    @ExceptionHandler({
            MethodArgumentNotValidException.class,
            ConstraintViolationException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    ResponseEntity<ApiResponse<Void>> handleValidation(Exception exception) {
        return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure(new ApiError(ErrorCode.INVALID_REQUEST.name(), "Invalid request")));
    }
}
