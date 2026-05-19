package com.wcms.auth.infra.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.wcms.auth.application.AuthTokenProperties;
import com.wcms.auth.domain.account.AccountRole;
import com.wcms.auth.domain.account.AuthAccount;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.BadCredentialsException;

class JwtTokenVerifierTests {

    private static final Instant NOW = Instant.parse("2026-05-19T00:00:00Z");

    private final AuthTokenProperties tokenProperties = new AuthTokenProperties(
            "wcms-test",
            "01234567890123456789012345678901",
            Duration.ofMinutes(15),
            Duration.ofDays(14)
    );
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final JwtTokenIssuer issuer = new JwtTokenIssuer(tokenProperties);
    private final JwtTokenVerifier verifier = new JwtTokenVerifier(tokenProperties, clock);

    @Test
    void verifyReturnsAuthenticatedUserFromValidAccessToken() {
        AuthAccount account = AuthAccount.create(
                UUID.fromString("00000000-0000-4000-8000-000000000001"),
                "platform-admin",
                "platform-admin@wcms.local",
                "encoded",
                AccountRole.SUPER_ADMIN
        );
        String token = issuer.issue(account, NOW).tokenValue();

        var user = verifier.verify(token);

        assertThat(user.userId()).isEqualTo(account.getId());
        assertThat(user.username()).isEqualTo("platform-admin");
        assertThat(user.role()).isEqualTo("SUPER_ADMIN");
        assertThat(user.tokenVersion()).isZero();
    }

    @Test
    void verifyRejectsExpiredAccessToken() {
        AuthAccount account = AuthAccount.create(
                UUID.randomUUID(),
                "platform-admin",
                "platform-admin@wcms.local",
                "encoded",
                AccountRole.SUPER_ADMIN
        );
        String token = issuer.issue(account, NOW.minus(Duration.ofMinutes(16))).tokenValue();

        assertThatThrownBy(() -> verifier.verify(token))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid access token");
    }

    @Test
    void verifyRejectsMalformedAccessToken() {
        assertThatThrownBy(() -> verifier.verify("not-a-jwt"))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid access token");
    }
}
