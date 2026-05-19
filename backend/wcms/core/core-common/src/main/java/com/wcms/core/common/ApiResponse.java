package com.wcms.core.common;

public record ApiResponse<T>(
        boolean succeeded,
        T data,
        ApiError error
) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }

    public static ApiResponse<Void> failure(ApiError error) {
        return new ApiResponse<>(false, null, error);
    }
}
