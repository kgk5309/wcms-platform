package com.wcms.user.application;

import com.wcms.user.domain.UserProfile;
import com.wcms.user.infra.persistence.UserProfileRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserProfileService {

    private final UserProfileRepository userProfileRepository;

    public UserProfileService(UserProfileRepository userProfileRepository) {
        this.userProfileRepository = userProfileRepository;
    }

    @Transactional
    public UserProfile create(CreateUserProfileCommand command) {
        if (userProfileRepository.existsByAuthAccountId(command.authAccountId())) {
            throw new IllegalArgumentException("user profile already exists for authAccountId");
        }
        if (userProfileRepository.existsByEmail(command.email())) {
            throw new IllegalArgumentException("user profile already exists for email");
        }

        UserProfile profile = UserProfile.create(
                UUID.randomUUID(),
                command.authAccountId(),
                command.displayName(),
                command.email(),
                command.phoneNumber(),
                command.role(),
                command.scopeType(),
                command.tenantId(),
                command.clientId()
        );
        return userProfileRepository.save(profile);
    }
}
