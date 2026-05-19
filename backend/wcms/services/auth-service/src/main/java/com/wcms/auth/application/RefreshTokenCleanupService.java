package com.wcms.auth.application;

import com.wcms.auth.infra.persistence.RefreshTokenSessionRepository;
import java.time.Clock;
import java.time.Instant;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RefreshTokenCleanupService {

    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final Clock clock;

    public RefreshTokenCleanupService(RefreshTokenSessionRepository refreshTokenSessionRepository, Clock clock) {
        this.refreshTokenSessionRepository = refreshTokenSessionRepository;
        this.clock = clock;
    }

    @Transactional
    public int deleteExpiredRefreshTokens() {
        Instant now = clock.instant();
        return refreshTokenSessionRepository.deleteExpiredAtOrBefore(now);
    }
}
