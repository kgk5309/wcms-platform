package com.wcms.auth.infra.scheduler;

import com.wcms.auth.application.AuthCleanupProperties;
import com.wcms.auth.application.RefreshTokenCleanupService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class RefreshTokenCleanupScheduler {

    private final AuthCleanupProperties cleanupProperties;
    private final RefreshTokenCleanupService cleanupService;

    public RefreshTokenCleanupScheduler(
            AuthCleanupProperties cleanupProperties,
            RefreshTokenCleanupService cleanupService
    ) {
        this.cleanupProperties = cleanupProperties;
        this.cleanupService = cleanupService;
    }

    @Scheduled(fixedDelayString = "${wcms.auth.cleanup.refresh-token-fixed-delay:PT1H}")
    void deleteExpiredRefreshTokens() {
        if (!cleanupProperties.enabled()) {
            return;
        }
        cleanupService.deleteExpiredRefreshTokens();
    }
}
