package com.wcms.user.application;

import com.wcms.user.domain.UserProfile;
import org.springframework.stereotype.Service;

@Service
public class UserOnboardingService {

    private final AuthServiceClient authServiceClient;
    private final UserProfileService userProfileService;

    public UserOnboardingService(AuthServiceClient authServiceClient, UserProfileService userProfileService) {
        this.authServiceClient = authServiceClient;
        this.userProfileService = userProfileService;
    }

    public OnboardedUser onboard(OnboardUserCommand command, String authorization) {
        AuthAccountResult authAccount = authServiceClient.createAccount(new CreateAuthAccountRequest(
                command.username(),
                command.email(),
                command.temporaryPassword(),
                command.role()
        ), authorization);

        try {
            UserProfile profile = userProfileService.create(new CreateUserProfileCommand(
                    authAccount.id(),
                    command.displayName(),
                    command.email(),
                    command.phoneNumber(),
                    command.role(),
                    command.scopeType(),
                    command.tenantId(),
                    command.clientId()
            ));
            return new OnboardedUser(authAccount, profile);
        } catch (RuntimeException exception) {
            authServiceClient.disableAccount(authAccount.id(), authorization);
            throw exception;
        }
    }
}
