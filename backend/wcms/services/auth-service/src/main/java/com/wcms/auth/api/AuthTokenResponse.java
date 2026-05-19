package com.wcms.auth.api;

import java.time.Instant;

public record AuthTokenResponse(
        String tokenType,
        String accessToken,
        long accessTokenExpiresIn,
        Instant accessTokenExpiresAt,
        String refreshToken,
        Instant refreshTokenExpiresAt,
        boolean passwordChangeRequired
) {
}
