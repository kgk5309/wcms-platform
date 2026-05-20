package com.wcms.user.application;

import com.wcms.user.domain.UserRole;
import com.wcms.user.domain.UserScopeType;
import java.util.UUID;

public record OnboardUserCommand(
        String username,
        String email,
        String temporaryPassword,
        String displayName,
        String phoneNumber,
        UserRole role,
        UserScopeType scopeType,
        UUID tenantId,
        UUID clientId
) {
}
