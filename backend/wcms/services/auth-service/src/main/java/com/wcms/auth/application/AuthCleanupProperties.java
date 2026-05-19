package com.wcms.auth.application;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wcms.auth.cleanup")
public record AuthCleanupProperties(
        boolean enabled,
        Duration refreshTokenFixedDelay
) {
    public AuthCleanupProperties {
        if (refreshTokenFixedDelay == null || refreshTokenFixedDelay.isNegative() || refreshTokenFixedDelay.isZero()) {
            refreshTokenFixedDelay = Duration.ofHours(1);
        }
    }
}
