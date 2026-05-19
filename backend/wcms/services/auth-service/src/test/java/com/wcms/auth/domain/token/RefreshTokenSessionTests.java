package com.wcms.auth.domain.token;

import static org.assertj.core.api.Assertions.assertThat;

import com.wcms.auth.domain.token.RefreshTokenSession;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class RefreshTokenSessionTests {

    @Test
    void issuedRefreshTokenIsActiveUntilExpiresOrRevoked() {
        Instant now = Instant.parse("2026-05-19T00:00:00Z");
        RefreshTokenSession session = RefreshTokenSession.issue(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "token-hash",
                now.plusSeconds(60),
                "127.0.0.1",
                "test-agent"
        );

        assertThat(session.isActiveAt(now)).isTrue();

        session.revoke(now.plusSeconds(10));

        assertThat(session.isActiveAt(now.plusSeconds(11))).isFalse();
    }
}
