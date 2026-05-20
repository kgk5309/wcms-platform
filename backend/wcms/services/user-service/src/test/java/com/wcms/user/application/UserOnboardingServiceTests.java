package com.wcms.user.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcms.user.domain.UserProfile;
import com.wcms.user.domain.UserRole;
import com.wcms.user.domain.UserScopeType;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class UserOnboardingServiceTests {

    private static final String AUTHORIZATION = "Bearer access-token";

    private final AuthServiceClient authServiceClient = org.mockito.Mockito.mock(AuthServiceClient.class);
    private final UserProfileService userProfileService = org.mockito.Mockito.mock(UserProfileService.class);
    private final UserOnboardingService userOnboardingService =
            new UserOnboardingService(authServiceClient, userProfileService);

    @Test
    void onboardCreatesAuthAccountAndUserProfile() {
        UUID authAccountId = UUID.randomUUID();
        OnboardUserCommand command = command();
        AuthAccountResult authAccount = authAccount(authAccountId);
        UserProfile profile = UserProfile.create(
                UUID.randomUUID(),
                authAccountId,
                command.displayName(),
                command.email(),
                command.phoneNumber(),
                command.role(),
                command.scopeType(),
                command.tenantId(),
                command.clientId()
        );
        when(authServiceClient.createAccount(new CreateAuthAccountRequest(
                command.username(),
                command.email(),
                command.temporaryPassword(),
                command.role()
        ), AUTHORIZATION)).thenReturn(authAccount);
        when(userProfileService.create(new CreateUserProfileCommand(
                authAccountId,
                command.displayName(),
                command.email(),
                command.phoneNumber(),
                command.role(),
                command.scopeType(),
                command.tenantId(),
                command.clientId()
        ))).thenReturn(profile);

        OnboardedUser onboardedUser = userOnboardingService.onboard(command, AUTHORIZATION);

        assertThat(onboardedUser.authAccount().id()).isEqualTo(authAccountId);
        assertThat(onboardedUser.profile().getAuthAccountId()).isEqualTo(authAccountId);
    }

    @Test
    void onboardDisablesAuthAccountWhenProfileCreationFails() {
        UUID authAccountId = UUID.randomUUID();
        OnboardUserCommand command = command();
        AuthAccountResult authAccount = authAccount(authAccountId);
        when(authServiceClient.createAccount(new CreateAuthAccountRequest(
                command.username(),
                command.email(),
                command.temporaryPassword(),
                command.role()
        ), AUTHORIZATION)).thenReturn(authAccount);
        when(userProfileService.create(org.mockito.ArgumentMatchers.any(CreateUserProfileCommand.class)))
                .thenThrow(new DuplicateUserProfileException("user profile already exists for email"));

        assertThatThrownBy(() -> userOnboardingService.onboard(command, AUTHORIZATION))
                .isInstanceOf(DuplicateUserProfileException.class);

        verify(authServiceClient).disableAccount(authAccountId, AUTHORIZATION);
    }

    private static OnboardUserCommand command() {
        return new OnboardUserCommand(
                "platform-manager",
                "platform-manager@wcms.local",
                "TempPassword123!",
                "Platform Manager",
                null,
                UserRole.PLATFORM_MASTER,
                UserScopeType.PLATFORM,
                null,
                null
        );
    }

    private static AuthAccountResult authAccount(UUID authAccountId) {
        return new AuthAccountResult(
                authAccountId,
                "platform-manager",
                "platform-manager@wcms.local",
                UserRole.PLATFORM_MASTER,
                "ACTIVE",
                true,
                0L
        );
    }
}
