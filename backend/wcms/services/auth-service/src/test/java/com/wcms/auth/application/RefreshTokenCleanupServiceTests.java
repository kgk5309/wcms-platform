package com.wcms.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcms.auth.infra.persistence.RefreshTokenSessionRepository;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import org.junit.jupiter.api.Test;

class RefreshTokenCleanupServiceTests {

    private static final Instant NOW = Instant.parse("2026-05-19T00:00:00Z");

    private final RefreshTokenSessionRepository refreshTokenSessionRepository =
            org.mockito.Mockito.mock(RefreshTokenSessionRepository.class);
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);
    private final RefreshTokenCleanupService cleanupService =
            new RefreshTokenCleanupService(refreshTokenSessionRepository, clock);

    @Test
    void deleteExpiredRefreshTokensDeletesSessionsExpiredAtOrBeforeNow() {
        when(refreshTokenSessionRepository.deleteExpiredAtOrBefore(NOW)).thenReturn(3);

        int deletedCount = cleanupService.deleteExpiredRefreshTokens();

        assertThat(deletedCount).isEqualTo(3);
        verify(refreshTokenSessionRepository).deleteExpiredAtOrBefore(NOW);
    }
}
