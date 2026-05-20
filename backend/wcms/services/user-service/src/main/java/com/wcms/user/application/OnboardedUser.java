package com.wcms.user.application;

import com.wcms.user.domain.UserProfile;

public record OnboardedUser(
        AuthAccountResult authAccount,
        UserProfile profile
) {
}
