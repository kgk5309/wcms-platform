package com.wcms.auth.domain.token;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "refresh_token_sessions")
public class RefreshTokenSession {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID accountId;

    @Column(nullable = false, unique = true, length = 128)
    private String tokenHash;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant revokedAt;

    @Column(length = 64)
    private String createdByIp;

    @Column(length = 512)
    private String userAgent;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected RefreshTokenSession() {
    }

    private RefreshTokenSession(
            UUID id,
            UUID accountId,
            String tokenHash,
            Instant expiresAt,
            String createdByIp,
            String userAgent
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.accountId = Objects.requireNonNull(accountId, "accountId must not be null");
        this.tokenHash = requireText(tokenHash, "tokenHash");
        this.expiresAt = Objects.requireNonNull(expiresAt, "expiresAt must not be null");
        this.createdByIp = createdByIp;
        this.userAgent = userAgent;
    }

    public static RefreshTokenSession issue(
            UUID id,
            UUID accountId,
            String tokenHash,
            Instant expiresAt,
            String createdByIp,
            String userAgent
    ) {
        return new RefreshTokenSession(id, accountId, tokenHash, expiresAt, createdByIp, userAgent);
    }

    public boolean isActiveAt(Instant now) {
        Objects.requireNonNull(now, "now must not be null");
        return revokedAt == null && expiresAt.isAfter(now);
    }

    public void revoke(Instant revokedAt) {
        this.revokedAt = Objects.requireNonNull(revokedAt, "revokedAt must not be null");
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value;
    }

    @PrePersist
    void prePersist() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    void preUpdate() {
        this.updatedAt = Instant.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAccountId() {
        return accountId;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public String getCreatedByIp() {
        return createdByIp;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
