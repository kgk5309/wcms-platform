package com.wcms.user.application;

import com.wcms.user.domain.UserRole;

public record ChangeUserRoleCommand(
        UserRole role
) {
}
