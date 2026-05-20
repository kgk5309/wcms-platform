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
                .isInstanceOf(IllegalArgumentException.class)
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
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("user profile already exists for email");
    }
}
