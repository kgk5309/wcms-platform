package com.wcms.user.api;

import com.wcms.user.domain.UserProfile;
import com.wcms.user.domain.UserRole;
import com.wcms.user.domain.UserScopeType;
import com.wcms.user.domain.UserStatus;
import java.time.Instant;
import java.util.UUID;

public record UserProfileResponse(
        UUID id,
        UUID authAccountId,
        String displayName,
        String email,
        String phoneNumber,
        UserRole role,
        UserScopeType scopeType,
        UUID tenantId,
        UUID clientId,
        UserStatus status,
        Instant createdAt,
        Instant updatedAt
) {

    static UserProfileResponse from(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getAuthAccountId(),
                profile.getDisplayName(),
                profile.getEmail(),
                profile.getPhoneNumber(),
                profile.getRole(),
                profile.getScopeType(),
                profile.getTenantId(),
                profile.getClientId(),
                profile.getStatus(),
                profile.getCreatedAt(),
                profile.getUpdatedAt()
        );
    }
}
