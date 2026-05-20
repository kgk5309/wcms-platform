package com.wcms.user.api;

import com.wcms.user.application.MoveUserScopeCommand;
import com.wcms.user.domain.UserScopeType;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record MoveUserScopeRequest(
        @NotNull UserScopeType scopeType,
        UUID tenantId,
        UUID clientId
) {

    MoveUserScopeCommand toCommand() {
        return new MoveUserScopeCommand(scopeType, tenantId, clientId);
    }
}
