package com.wcms.user.infra.client;

import com.wcms.core.common.ApiResponse;
import com.wcms.user.application.AuthAccountResult;
import com.wcms.user.application.AuthServiceClient;
import com.wcms.user.application.AuthServiceProperties;
import com.wcms.user.application.CreateAuthAccountRequest;
import java.util.UUID;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AuthServiceRestClient implements AuthServiceClient {

    private final RestClient restClient;

    public AuthServiceRestClient(RestClient.Builder restClientBuilder, AuthServiceProperties properties) {
        this.restClient = restClientBuilder
                .baseUrl(properties.baseUrl())
                .build();
    }

    @Override
    public AuthAccountResult createAccount(CreateAuthAccountRequest request, String authorization) {
        ApiResponse<AuthAccountResult> response = restClient.post()
                .uri("/api/auth/accounts")
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .body(request)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return requireData(response);
    }

    @Override
    public AuthAccountResult disableAccount(UUID accountId, String authorization) {
        ApiResponse<AuthAccountResult> response = restClient.post()
                .uri("/api/auth/accounts/{id}/disable", accountId)
                .header(HttpHeaders.AUTHORIZATION, authorization)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
        return requireData(response);
    }

    private static AuthAccountResult requireData(ApiResponse<AuthAccountResult> response) {
        if (response == null || !response.succeeded() || response.data() == null) {
            throw new IllegalStateException("auth service request failed");
        }
        return response.data();
    }
}
