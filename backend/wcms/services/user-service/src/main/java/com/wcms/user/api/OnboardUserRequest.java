package com.wcms.user.api;

import com.wcms.user.application.OnboardUserCommand;
import com.wcms.user.domain.UserRole;
import com.wcms.user.domain.UserScopeType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.UUID;

public record OnboardUserRequest(
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 72) String temporaryPassword,
        @NotBlank String displayName,
        String phoneNumber,
        @NotNull UserRole role,
        @NotNull UserScopeType scopeType,
        UUID tenantId,
        UUID clientId
) {

    OnboardUserCommand toCommand() {
        return new OnboardUserCommand(
                username,
                email,
                temporaryPassword,
                displayName,
                phoneNumber,
                role,
                scopeType,
                tenantId,
                clientId
        );
    }
}
