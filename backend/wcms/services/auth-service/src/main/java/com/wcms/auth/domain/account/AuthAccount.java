package com.wcms.auth.domain.account;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "auth_accounts")
public class AuthAccount {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(nullable = false, unique = true, length = 80)
    private String username;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private AccountRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status;

    @Column(nullable = false)
    private boolean passwordChangeRequired;

    @Column(nullable = false)
    private long tokenVersion;

    @Column(nullable = false)
    private int failedLoginCount;

    private Instant lockedUntil;

    private Instant lastLoginAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected AuthAccount() {
    }

    private AuthAccount(UUID id, String username, String email, String passwordHash, AccountRole role) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.username = requireText(username, "username");
        this.email = requireText(email, "email");
        this.passwordHash = requireText(passwordHash, "passwordHash");
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.status = AccountStatus.ACTIVE;
        this.passwordChangeRequired = true;
        this.tokenVersion = 0L;
        this.failedLoginCount = 0;
    }

    public static AuthAccount create(
            UUID id,
            String username,
            String email,
            String temporaryPasswordHash,
            AccountRole role
    ) {
        return new AuthAccount(id, username, email, temporaryPasswordHash, role);
    }

    public boolean canLoginAt(Instant now) {
        if (status == AccountStatus.DISABLED) {
            return false;
        }
        return lockedUntil == null || !lockedUntil.isAfter(now);
    }

    public void recordLoginSuccess(Instant loggedInAt) {
        this.failedLoginCount = 0;
        this.lockedUntil = null;
        this.lastLoginAt = Objects.requireNonNull(loggedInAt, "loggedInAt must not be null");
    }

    public void recordLoginFailure(Instant failedAt, int maxFailures, Duration lockDuration) {
        if (maxFailures < 1) {
            throw new IllegalArgumentException("maxFailures must be positive");
        }
        Objects.requireNonNull(failedAt, "failedAt must not be null");
        Objects.requireNonNull(lockDuration, "lockDuration must not be null");

        this.failedLoginCount += 1;
        if (this.failedLoginCount >= maxFailures) {
            this.status = AccountStatus.LOCKED;
            this.lockedUntil = failedAt.plus(lockDuration);
        }
    }

    public void unlockIfLockExpired(Instant now) {
        if (status == AccountStatus.LOCKED && lockedUntil != null && !lockedUntil.isAfter(now)) {
            this.status = AccountStatus.ACTIVE;
            this.lockedUntil = null;
            this.failedLoginCount = 0;
        }
    }

    public void changePassword(String newPasswordHash) {
        this.passwordHash = requireText(newPasswordHash, "newPasswordHash");
        this.passwordChangeRequired = false;
        increaseTokenVersion();
    }

    public void resetTemporaryPassword(String temporaryPasswordHash) {
        this.passwordHash = requireText(temporaryPasswordHash, "temporaryPasswordHash");
        this.passwordChangeRequired = true;
        this.failedLoginCount = 0;
        this.lockedUntil = null;
        this.status = AccountStatus.ACTIVE;
        increaseTokenVersion();
    }

    public void disable() {
        this.status = AccountStatus.DISABLED;
        this.lockedUntil = null;
        this.failedLoginCount = 0;
        increaseTokenVersion();
    }

    public void activate() {
        this.status = AccountStatus.ACTIVE;
        this.lockedUntil = null;
        this.failedLoginCount = 0;
        increaseTokenVersion();
    }

    public void increaseTokenVersion() {
        this.tokenVersion += 1;
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

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public AccountRole getRole() {
        return role;
    }

    public AccountStatus getStatus() {
        return status;
    }

    public boolean isPasswordChangeRequired() {
        return passwordChangeRequired;
    }

    public long getTokenVersion() {
        return tokenVersion;
    }

    public int getFailedLoginCount() {
        return failedLoginCount;
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public Instant getLastLoginAt() {
        return lastLoginAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
