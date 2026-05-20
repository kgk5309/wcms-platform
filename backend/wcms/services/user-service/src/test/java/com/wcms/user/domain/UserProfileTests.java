package com.wcms.user.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserProfileTests {

    @Test
    void createPlatformUserProfile() {
        UUID authAccountId = UUID.randomUUID();

        UserProfile profile = UserProfile.create(
                UUID.randomUUID(),
                authAccountId,
                "Platform Admin",
                "platform-admin@wcms.local",
                "010-0000-0000",
                UserRole.SUPER_ADMIN,
                UserScopeType.PLATFORM,
                null,
                null
        );

        assertThat(profile.getAuthAccountId()).isEqualTo(authAccountId);
        assertThat(profile.getDisplayName()).isEqualTo("Platform Admin");
        assertThat(profile.getStatus()).isEqualTo(UserStatus.ACTIVE);
    }

    @Test
    void platformUserCannotHaveTenantOrClientScopeIds() {
        assertThatThrownBy(() -> UserProfile.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Platform User",
                "platform-user@wcms.local",
                null,
                UserRole.PLATFORM_USER,
                UserScopeType.PLATFORM,
                UUID.randomUUID(),
                null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("platform scoped user must not have tenantId or clientId");
    }

    @Test
    void tenantUserMustHaveTenantIdOnly() {
        assertThatThrownBy(() -> UserProfile.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tenant User",
                "tenant-user@wcms.local",
                null,
                UserRole.TENANT_USER,
                UserScopeType.TENANT,
                null,
                null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("tenant scoped user must have tenantId only");
    }

    @Test
    void clientUserMustHaveTenantIdAndClientId() {
        assertThatThrownBy(() -> UserProfile.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Client User",
                "client-user@wcms.local",
                null,
                UserRole.CLIENT_USER,
                UserScopeType.CLIENT,
                UUID.randomUUID(),
                null
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("client scoped user must have tenantId and clientId");
    }

    @Test
    void canMoveScopeWhenTargetScopeIsValid() {
        UserProfile profile = UserProfile.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Tenant User",
                "tenant-user@wcms.local",
                null,
                UserRole.TENANT_USER,
                UserScopeType.TENANT,
                UUID.randomUUID(),
                null
        );
        UUID tenantId = UUID.randomUUID();
        UUID clientId = UUID.randomUUID();

        profile.moveScope(UserScopeType.CLIENT, tenantId, clientId);

        assertThat(profile.getScopeType()).isEqualTo(UserScopeType.CLIENT);
        assertThat(profile.getTenantId()).isEqualTo(tenantId);
        assertThat(profile.getClientId()).isEqualTo(clientId);
    }
}
