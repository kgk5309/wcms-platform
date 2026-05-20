package com.wcms.user.application;

import java.util.UUID;

public interface AuthServiceClient {

    AuthAccountResult createAccount(CreateAuthAccountRequest request, String authorization);

    AuthAccountResult disableAccount(UUID accountId, String authorization);
}
