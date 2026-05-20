package com.wcms.auth.application;

import com.wcms.auth.domain.account.AccountRole;

public record CreateAuthAccountCommand(
        String username,
        String email,
        String temporaryPassword,
        AccountRole role
) {
}
