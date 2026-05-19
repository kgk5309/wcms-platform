package com.wcms.auth.application;

import java.time.Duration;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wcms.auth.login")
public record AuthLoginProperties(
        int maxFailures,
        Duration lockDuration
) {
    public AuthLoginProperties {
        if (maxFailures < 1) {
            maxFailures = 5;
        }
        if (lockDuration == null) {
            lockDuration = Duration.ofMinutes(10);
        }
    }
}
