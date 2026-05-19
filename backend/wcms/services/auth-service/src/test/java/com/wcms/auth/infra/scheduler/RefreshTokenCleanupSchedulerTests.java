package com.wcms.auth.infra.scheduler;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.wcms.auth.application.AuthCleanupProperties;
import com.wcms.auth.application.RefreshTokenCleanupService;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class RefreshTokenCleanupSchedulerTests {

    private final RefreshTokenCleanupService cleanupService =
            org.mockito.Mockito.mock(RefreshTokenCleanupService.class);

    @Test
    void deleteExpiredRefreshTokensRunsWhenEnabled() {
        RefreshTokenCleanupScheduler scheduler = new RefreshTokenCleanupScheduler(
                new AuthCleanupProperties(true, Duration.ofHours(1)),
                cleanupService
        );

        scheduler.deleteExpiredRefreshTokens();

        verify(cleanupService).deleteExpiredRefreshTokens();
    }

    @Test
    void deleteExpiredRefreshTokensDoesNothingWhenDisabled() {
        RefreshTokenCleanupScheduler scheduler = new RefreshTokenCleanupScheduler(
                new AuthCleanupProperties(false, Duration.ofHours(1)),
                cleanupService
        );

        scheduler.deleteExpiredRefreshTokens();

        verify(cleanupService, never()).deleteExpiredRefreshTokens();
    }
}
