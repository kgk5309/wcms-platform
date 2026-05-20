package com.wcms.user.api;

import com.wcms.user.application.CreateUserProfileCommand;
import com.wcms.user.domain.UserRole;
import com.wcms.user.domain.UserScopeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateUserProfileRequest(
        @NotNull UUID authAccountId,
        @NotBlank String displayName,
        @NotBlank @Email String email,
        String phoneNumber,
        @NotNull UserRole role,
        @NotNull UserScopeType scopeType,
        UUID tenantId,
        UUID clientId
) {

    CreateUserProfileCommand toCommand() {
        return new CreateUserProfileCommand(
                authAccountId,
                displayName,
                email,
                phoneNumber,
                role,
                scopeType,
                tenantId,
                clientId
        );
    }
}
