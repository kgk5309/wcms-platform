package com.wcms.core.security;

import java.util.UUID;

public record AuthenticatedUser(
        UUID userId,
        String username,
        String role,
        long tokenVersion
) {
}
