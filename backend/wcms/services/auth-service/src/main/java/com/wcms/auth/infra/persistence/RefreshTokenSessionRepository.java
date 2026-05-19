package com.wcms.auth.infra.persistence;

import com.wcms.auth.domain.token.RefreshTokenSession;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RefreshTokenSessionRepository extends JpaRepository<RefreshTokenSession, UUID> {

    Optional<RefreshTokenSession> findByTokenHash(String tokenHash);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from RefreshTokenSession session where session.expiresAt <= :now")
    int deleteExpiredAtOrBefore(@Param("now") Instant now);
}
