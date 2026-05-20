package com.wcms.user.api;

import com.wcms.user.application.UpdateUserProfileCommand;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UpdateUserProfileRequest(
        @NotBlank String displayName,
        @NotBlank @Email String email,
        String phoneNumber
) {

    UpdateUserProfileCommand toCommand() {
        return new UpdateUserProfileCommand(displayName, email, phoneNumber);
    }
}
