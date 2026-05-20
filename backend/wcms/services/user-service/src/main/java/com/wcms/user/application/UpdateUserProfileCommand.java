package com.wcms.user.application;

public record UpdateUserProfileCommand(
        String displayName,
        String email,
        String phoneNumber
) {
}
