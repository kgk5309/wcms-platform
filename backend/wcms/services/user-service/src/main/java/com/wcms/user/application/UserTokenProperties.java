package com.wcms.user.application;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "wcms.user.token")
public record UserTokenProperties(
        String issuer,
        String secret
) {
    public UserTokenProperties {
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalArgumentException("wcms.user.token.issuer must not be blank");
        }
        if (secret == null || secret.isBlank()) {
            throw new IllegalArgumentException("wcms.user.token.secret must not be blank");
        }
    }
}
