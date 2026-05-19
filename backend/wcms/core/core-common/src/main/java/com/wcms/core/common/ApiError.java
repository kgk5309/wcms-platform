package com.wcms.core.common;

public record ApiError(
        String code,
        String message
) {
}
