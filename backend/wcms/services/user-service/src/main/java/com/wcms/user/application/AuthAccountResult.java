package com.wcms.user.application;

import com.wcms.user.domain.UserRole;
import java.util.UUID;

public record AuthAccountResult(
        UUID id,
        String username,
        String email,
        UserRole role,
        String status,
        boolean passwordChangeRequired,
        long tokenVersion
) {
}
