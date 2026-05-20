package com.wcms.user.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wcms.user.auth-service")
public record AuthServiceProperties(
        String baseUrl
) {
    public AuthServiceProperties {
        if (baseUrl == null || baseUrl.isBlank()) {
            baseUrl = "http://localhost:18081";
        }
    }
}
