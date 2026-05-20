package com.wcms.auth.api;

import com.wcms.auth.application.CreateAuthAccountCommand;
import com.wcms.auth.domain.account.AccountRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateAuthAccountRequest(
        @NotBlank @Size(max = 80) String username,
        @NotBlank @Email @Size(max = 255) String email,
        @NotBlank @Size(min = 8, max = 72) String temporaryPassword,
        @NotNull AccountRole role
) {

    CreateAuthAccountCommand toCommand() {
        return new CreateAuthAccountCommand(username, email, temporaryPassword, role);
    }
}
