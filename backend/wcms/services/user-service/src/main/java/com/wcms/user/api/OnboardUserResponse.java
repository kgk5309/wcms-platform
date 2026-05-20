package com.wcms.user.api;

import com.wcms.user.application.OnboardedUser;

public record OnboardUserResponse(
        AuthAccountResponse authAccount,
        UserProfileResponse profile
) {

    static OnboardUserResponse from(OnboardedUser onboardedUser) {
        return new OnboardUserResponse(
                AuthAccountResponse.from(onboardedUser.authAccount()),
                UserProfileResponse.from(onboardedUser.profile())
        );
    }
}
