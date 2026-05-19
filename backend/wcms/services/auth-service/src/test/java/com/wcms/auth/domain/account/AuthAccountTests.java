package com.wcms.auth.domain.account;

import static org.assertj.core.api.Assertions.assertThat;

import com.wcms.auth.domain.account.AccountRole;
import com.wcms.auth.domain.account.AccountStatus;
import com.wcms.auth.domain.account.AuthAccount;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class AuthAccountTests {

    @Test
    void createdAccountRequiresPasswordChange() {
        AuthAccount account = AuthAccount.create(
                UUID.randomUUID(),
                "platform-admin",
                "admin@example.com",
                "encoded-password",
                AccountRole.PLATFORM_MASTER
        );

        assertThat(account.getStatus()).isEqualTo(AccountStatus.ACTIVE);
        assertThat(account.isPasswordChangeRequired()).isTrue();
        assertThat(account.getTokenVersion()).isZero();
    }

    @Test
    void changePasswordClearsPasswordChangeRequirementAndInvalidatesTokens() {
        AuthAccount account = AuthAccount.create(
                UUID.randomUUID(),
                "platform-admin",
                "admin@example.com",
                "encoded-password",
                AccountRole.PLATFORM_MASTER
        );

        account.changePassword("new-encoded-password");

        assertThat(account.isPasswordChangeRequired()).isFalse();
        assertThat(account.getPasswordHash()).isEqualTo("new-encoded-password");
        assertThat(account.getTokenVersion()).isEqualTo(1);
    }

    @Test
    void repeatedLoginFailuresLockAccount() {
        AuthAccount account = AuthAccount.create(
                UUID.randomUUID(),
                "platform-admin",
                "admin@example.com",
                "encoded-password",
                AccountRole.PLATFORM_MASTER
        );
        Instant now = Instant.parse("2026-05-19T00:00:00Z");

        account.recordLoginFailure(now, 2, Duration.ofMinutes(10));
        account.recordLoginFailure(now, 2, Duration.ofMinutes(10));

        assertThat(account.getStatus()).isEqualTo(AccountStatus.LOCKED);
        assertThat(account.canLoginAt(now.plusSeconds(1))).isFalse();
        assertThat(account.getLockedUntil()).isEqualTo(now.plus(Duration.ofMinutes(10)));
    }
}
