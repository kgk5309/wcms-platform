package com.wcms.user.application;

import com.wcms.user.domain.UserRole;
import com.wcms.user.domain.UserScopeType;
import java.util.UUID;

public record CreateUserProfileCommand(
        UUID authAccountId,
        String displayName,
        String email,
        String phoneNumber,
        UserRole role,
        UserScopeType scopeType,
        UUID tenantId,
        UUID clientId
) {
}
