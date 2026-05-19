package com.wcms.auth.infra.security;

import java.util.UUID;

public record AuthPrincipal(
        UUID userId,
        String username,
        String role,
        long tokenVersion
) {
}
