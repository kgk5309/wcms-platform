package com.wcms.user.application;

import com.wcms.user.domain.UserScopeType;
import java.util.UUID;

public record MoveUserScopeCommand(
        UserScopeType scopeType,
        UUID tenantId,
        UUID clientId
) {
}
