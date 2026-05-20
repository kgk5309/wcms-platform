package com.wcms.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "user_profiles")
public class UserProfile {

    @Id
    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(nullable = false, updatable = false, columnDefinition = "BINARY(16)")
    private UUID id;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(nullable = false, updatable = false, unique = true, columnDefinition = "BINARY(16)")
    private UUID authAccountId;

    @Column(nullable = false, length = 100)
    private String displayName;

    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @Column(length = 40)
    private String phoneNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 40)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserScopeType scopeType;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    private UUID tenantId;

    @JdbcTypeCode(SqlTypes.BINARY)
    @Column(columnDefinition = "BINARY(16)")
    private UUID clientId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserStatus status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    protected UserProfile() {
    }

    private UserProfile(
            UUID id,
            UUID authAccountId,
            String displayName,
            String email,
            String phoneNumber,
            UserRole role,
            UserScopeType scopeType,
            UUID tenantId,
            UUID clientId
    ) {
        this.id = Objects.requireNonNull(id, "id must not be null");
        this.authAccountId = Objects.requireNonNull(authAccountId, "authAccountId must not be null");
        this.displayName = requireText(displayName, "displayName");
        this.email = requireText(email, "email");
        this.phoneNumber = normalizeOptionalText(phoneNumber);
        this.role = Objects.requireNonNull(role, "role must not be null");
        this.scopeType = Objects.requireNonNull(scopeType, "scopeType must not be null");
        this.tenantId = tenantId;
        this.clientId = clientId;
        validateScope(scopeType, tenantId, clientId);
        this.status = UserStatus.ACTIVE;
    }

    public static UserProfile create(
            UUID id,
            UUID authAccountId,
            String displayName,
            String email,
            String phoneNumber,
            UserRole role,
            UserScopeType scopeType,
            UUID tenantId,
            UUID clientId
    ) {
        return new UserProfile(id, authAccountId, displayName, email, phoneNumber, role, scopeType, tenantId, clientId);
    }

    public void updateProfile(String displayName, String email, String phoneNumber) {
        this.displayName = requireText(displayName, "displayName");
        this.email = requireText(email, "email");
        this.phoneNumber = normalizeOptionalText(phoneNumber);
    }

    public void changeRole(UserRole role) {
        this.role = Objects.requireNonNull(role, "role must not be null");
    }

    public void moveScope(UserScopeType scopeType, UUID tenantId, UUID clientId) {
        Objects.requireNonNull(scopeType, "scopeType must not be null");
        validateScope(scopeType, tenantId, clientId);
        this.scopeType = scopeType;
        this.tenantId = tenantId;
        this.clientId = clientId;
    }

    public void disable() {
        this.status = UserStatus.DISABLED;
    }

    public void activate() {
        this.status = UserStatus.ACTIVE;
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

    private static void validateScope(UserScopeType scopeType, UUID tenantId, UUID clientId) {
        if (scopeType == UserScopeType.PLATFORM && (tenantId != null || clientId != null)) {
            throw new IllegalArgumentException("platform scoped user must not have tenantId or clientId");
        }
        if (scopeType == UserScopeType.TENANT && (tenantId == null || clientId != null)) {
            throw new IllegalArgumentException("tenant scoped user must have tenantId only");
        }
        if (scopeType == UserScopeType.CLIENT && (tenantId == null || clientId == null)) {
            throw new IllegalArgumentException("client scoped user must have tenantId and clientId");
        }
    }

    private static String requireText(String value, String name) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(name + " must not be blank");
        }
        return value.trim();
    }

    private static String normalizeOptionalText(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    public UUID getId() {
        return id;
    }

    public UUID getAuthAccountId() {
        return authAccountId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public UserRole getRole() {
        return role;
    }

    public UserScopeType getScopeType() {
        return scopeType;
    }

    public UUID getTenantId() {
        return tenantId;
    }

    public UUID getClientId() {
        return clientId;
    }

    public UserStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }
}
