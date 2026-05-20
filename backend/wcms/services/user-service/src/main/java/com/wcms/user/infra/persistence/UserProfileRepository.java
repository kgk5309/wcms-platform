package com.wcms.user.infra.persistence;

import com.wcms.user.domain.UserProfile;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

    Optional<UserProfile> findByAuthAccountId(UUID authAccountId);

    boolean existsByAuthAccountId(UUID authAccountId);

    boolean existsByEmail(String email);
}
