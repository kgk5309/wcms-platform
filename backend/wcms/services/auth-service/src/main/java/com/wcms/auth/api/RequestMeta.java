package com.wcms.auth.api;

public record RequestMeta(
        String ipAddress,
        String userAgent
) {
}
