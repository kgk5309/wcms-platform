package com.wcms.user.api;

import com.wcms.user.application.AuthAccountResult;
import com.wcms.user.domain.UserRole;
import java.util.UUID;

public record AuthAccountResponse(
        UUID id,
        String username,
        String email,
        UserRole role,
        String status,
        boolean passwordChangeRequired,
        long tokenVersion
) {

    static AuthAccountResponse from(AuthAccountResult result) {
        return new AuthAccountResponse(
                result.id(),
                result.username(),
                result.email(),
                result.role(),
                result.status(),
                result.passwordChangeRequired(),
                result.tokenVersion()
        );
    }
}
