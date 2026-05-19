package com.wcms.auth.application;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wcms.auth.token")
public record AuthTokenProperties(
        String issuer,
        String secret,
        Duration accessTokenTtl,
        Duration refreshTokenTtl
) {
    public AuthTokenProperties {
        if (issuer == null || issuer.isBlank()) {
            issuer = "wcms-platform";
        }
        if (secret == null || secret.length() < 32) {
            secret = "wcms-local-dev-secret-must-be-rotated";
        }
        if (accessTokenTtl == null) {
            accessTokenTtl = Duration.ofMinutes(15);
        }
        if (refreshTokenTtl == null) {
            refreshTokenTtl = Duration.ofDays(14);
        }
    }
}
