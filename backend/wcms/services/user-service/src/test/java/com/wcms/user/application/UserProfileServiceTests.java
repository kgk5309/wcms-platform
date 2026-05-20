package com.wcms.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcms.user.domain.UserProfile;
import com.wcms.user.domain.UserRole;
import com.wcms.user.domain.UserScopeType;
import com.wcms.user.infra.persistence.UserProfileRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class UserProfileServiceTests {

    private final UserProfileRepository userProfileRepository = org.mockito.Mockito.mock(UserProfileRepository.class);
    private final UserProfileService userProfileService = new UserProfileService(userProfileRepository);

    @Test
    void createStoresUserProfile() {
        UUID authAccountId = UUID.randomUUID();
        CreateUserProfileCommand command = new CreateUserProfileCommand(
                authAccountId,
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.SUPER_ADMIN,
                UserScopeType.PLATFORM,
                null,
                null
        );
        when(userProfileRepository.save(any(UserProfile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserProfile profile = userProfileService.create(command);

        assertThat(profile.getAuthAccountId()).isEqualTo(authAccountId);
        assertThat(profile.getDisplayName()).isEqualTo("Platform Admin");

        ArgumentCaptor<UserProfile> captor = ArgumentCaptor.forClass(UserProfile.class);
        verify(userProfileRepository).save(captor.capture());
        assertThat(captor.getValue().getEmail()).isEqualTo("platform-admin@wcms.local");
    }

    @Test
    void createRejectsDuplicatedAuthAccountId() {
        UUID authAccountId = UUID.randomUUID();
        when(userProfileRepository.existsByAuthAccountId(authAccountId)).thenReturn(true);

        assertThatThrownBy(() -> userProfileService.create(new CreateUserProfileCommand(
                authAccountId,
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.SUPER_ADMIN,
                UserScopeType.PLATFORM,
                null,
                null
        )))
                .isInstanceOf(DuplicateUserProfileException.class)
                .hasMessage("user profile already exists for authAccountId");
    }

    @Test
    void createRejectsDuplicatedEmail() {
        String email = "platform-admin@wcms.local";
        when(userProfileRepository.existsByEmail(email)).thenReturn(true);

        assertThatThrownBy(() -> userProfileService.create(new CreateUserProfileCommand(
                UUID.randomUUID(),
                "Platform Admin",
                email,
                null,
                UserRole.SUPER_ADMIN,
                UserScopeType.PLATFORM,
                null,
                null
        )))
                .isInstanceOf(DuplicateUserProfileException.class)
                .hasMessage("user profile already exists for email");
    }

    @Test
    void updateProfileChangesBasicFields() {
        UUID profileId = UUID.randomUUID();
        UserProfile profile = UserProfile.create(
                profileId,
                UUID.randomUUID(),
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.SUPER_ADMIN,
                UserScopeType.PLATFORM,
                null,
                null
        );
        when(userProfileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        UserProfile updated = userProfileService.updateProfile(
                profileId,
                new UpdateUserProfileCommand("Root Admin", "root-admin@wcms.local", "010-0000-0000")
        );

        assertThat(updated.getDisplayName()).isEqualTo("Root Admin");
        assertThat(updated.getEmail()).isEqualTo("root-admin@wcms.local");
        assertThat(updated.getPhoneNumber()).isEqualTo("010-0000-0000");
    }

    @Test
    void updateProfileRejectsDuplicatedEmail() {
        UUID profileId = UUID.randomUUID();
        UserProfile profile = UserProfile.create(
                profileId,
                UUID.randomUUID(),
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.SUPER_ADMIN,
                UserScopeType.PLATFORM,
                null,
                null
        );
        when(userProfileRepository.findById(profileId)).thenReturn(Optional.of(profile));
        when(userProfileRepository.existsByEmailAndIdNot("root-admin@wcms.local", profileId)).thenReturn(true);

        assertThatThrownBy(() -> userProfileService.updateProfile(
                profileId,
                new UpdateUserProfileCommand("Root Admin", "root-admin@wcms.local", null)
        ))
                .isInstanceOf(DuplicateUserProfileException.class)
                .hasMessage("user profile already exists for email");
    }

    @Test
    void changeRoleUpdatesRole() {
        UUID profileId = UUID.randomUUID();
        UserProfile profile = UserProfile.create(
                profileId,
                UUID.randomUUID(),
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.PLATFORM_USER,
                UserScopeType.PLATFORM,
                null,
                null
        );
        when(userProfileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        UserProfile updated = userProfileService.changeRole(profileId, new ChangeUserRoleCommand(UserRole.SUPER_ADMIN));

        assertThat(updated.getRole()).isEqualTo(UserRole.SUPER_ADMIN);
    }

    @Test
    void moveScopeUpdatesScope() {
        UUID profileId = UUID.randomUUID();
        UUID tenantId = UUID.randomUUID();
        UserProfile profile = UserProfile.create(
                profileId,
                UUID.randomUUID(),
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.PLATFORM_USER,
                UserScopeType.PLATFORM,
                null,
                null
        );
        when(userProfileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        UserProfile updated = userProfileService.moveScope(
                profileId,
                new MoveUserScopeCommand(UserScopeType.TENANT, tenantId, null)
        );

        assertThat(updated.getScopeType()).isEqualTo(UserScopeType.TENANT);
        assertThat(updated.getTenantId()).isEqualTo(tenantId);
        assertThat(updated.getClientId()).isNull();
    }

    @Test
    void disableAndActivateChangeStatus() {
        UUID profileId = UUID.randomUUID();
        UserProfile profile = UserProfile.create(
                profileId,
                UUID.randomUUID(),
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.PLATFORM_USER,
                UserScopeType.PLATFORM,
                null,
                null
        );
        when(userProfileRepository.findById(profileId)).thenReturn(Optional.of(profile));

        assertThat(userProfileService.disable(profileId).getStatus().name()).isEqualTo("DISABLED");
        assertThat(userProfileService.activate(profileId).getStatus().name()).isEqualTo("ACTIVE");
    }
}
