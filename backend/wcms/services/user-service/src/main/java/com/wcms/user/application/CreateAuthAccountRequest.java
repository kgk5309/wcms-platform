package com.wcms.user.application;

import com.wcms.user.domain.UserRole;

public record CreateAuthAccountRequest(
        String username,
        String email,
        String temporaryPassword,
        UserRole role
) {
}
