package com.wcms.user.api;

import com.wcms.user.application.ChangeUserRoleCommand;
import com.wcms.user.domain.UserRole;
import jakarta.validation.constraints.NotNull;

public record ChangeUserRoleRequest(
        @NotNull UserRole role
) {

    ChangeUserRoleCommand toCommand() {
        return new ChangeUserRoleCommand(role);
    }
}
