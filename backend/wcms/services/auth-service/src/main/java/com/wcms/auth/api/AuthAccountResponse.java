package com.wcms.auth.api;

import com.wcms.auth.domain.account.AccountRole;
import com.wcms.auth.domain.account.AccountStatus;
import com.wcms.auth.domain.account.AuthAccount;
import java.time.Instant;
import java.util.UUID;

public record AuthAccountResponse(
        UUID id,
        String username,
        String email,
        AccountRole role,
        AccountStatus status,
        boolean passwordChangeRequired,
        long tokenVersion,
        Instant createdAt,
        Instant updatedAt
) {

    static AuthAccountResponse from(AuthAccount account) {
        return new AuthAccountResponse(
                account.getId(),
                account.getUsername(),
                account.getEmail(),
                account.getRole(),
                account.getStatus(),
                account.isPasswordChangeRequired(),
                account.getTokenVersion(),
                account.getCreatedAt(),
                account.getUpdatedAt()
        );
    }
}
