package com.wcms.user.application;

import com.wcms.user.domain.UserProfile;
import com.wcms.user.infra.persistence.UserProfileRepository;
import java.util.List;
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
            throw new DuplicateUserProfileException("user profile already exists for authAccountId");
        }
        if (userProfileRepository.existsByEmail(command.email())) {
            throw new DuplicateUserProfileException("user profile already exists for email");
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

    @Transactional(readOnly = true)
    public UserProfile get(UUID id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException("user profile not found"));
    }

    @Transactional(readOnly = true)
    public List<UserProfile> findAll() {
        return userProfileRepository.findAll();
    }

    @Transactional
    public UserProfile updateProfile(UUID id, UpdateUserProfileCommand command) {
        UserProfile profile = getForUpdate(id);
        if (userProfileRepository.existsByEmailAndIdNot(command.email(), id)) {
            throw new DuplicateUserProfileException("user profile already exists for email");
        }
        profile.updateProfile(command.displayName(), command.email(), command.phoneNumber());
        return profile;
    }

    @Transactional
    public UserProfile changeRole(UUID id, ChangeUserRoleCommand command) {
        UserProfile profile = getForUpdate(id);
        profile.changeRole(command.role());
        return profile;
    }

    @Transactional
    public UserProfile moveScope(UUID id, MoveUserScopeCommand command) {
        UserProfile profile = getForUpdate(id);
        profile.moveScope(command.scopeType(), command.tenantId(), command.clientId());
        return profile;
    }

    @Transactional
    public UserProfile disable(UUID id) {
        UserProfile profile = getForUpdate(id);
        profile.disable();
        return profile;
    }

    @Transactional
    public UserProfile activate(UUID id) {
        UserProfile profile = getForUpdate(id);
        profile.activate();
        return profile;
    }

    private UserProfile getForUpdate(UUID id) {
        return userProfileRepository.findById(id)
                .orElseThrow(() -> new UserProfileNotFoundException("user profile not found"));
    }
}
