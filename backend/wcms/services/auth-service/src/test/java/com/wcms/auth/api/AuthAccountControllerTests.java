package com.wcms.auth.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcms.auth.application.AuthService;
import com.wcms.auth.application.CreateAuthAccountCommand;
import com.wcms.auth.application.DuplicateAuthAccountException;
import com.wcms.auth.domain.account.AccountRole;
import com.wcms.auth.domain.account.AuthAccount;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-account-api-test;MODE=MariaDB;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "wcms.auth.token.issuer=wcms-test",
        "wcms.auth.token.secret=01234567890123456789012345678901"
})
@AutoConfigureMockMvc
class AuthAccountControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void createAccountReturnsCreatedAccountForSuperAdmin() throws Exception {
        UUID accountId = UUID.randomUUID();
        AuthAccount account = AuthAccount.create(
                accountId,
                "platform-manager",
                "platform-manager@wcms.local",
                "encoded",
                AccountRole.PLATFORM_MASTER
        );
        when(authService.createAccount(any(CreateAuthAccountCommand.class))).thenReturn(account);

        mockMvc.perform(post("/api/auth/accounts")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("SUPER_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "platform-manager",
                                  "email": "platform-manager@wcms.local",
                                  "temporaryPassword": "TempPassword123!",
                                  "role": "PLATFORM_MASTER"
                                }
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.succeeded").value(true))
                .andExpect(jsonPath("$.data.id").value(accountId.toString()))
                .andExpect(jsonPath("$.data.username").value("platform-manager"))
                .andExpect(jsonPath("$.data.role").value("PLATFORM_MASTER"))
                .andExpect(jsonPath("$.data.passwordChangeRequired").value(true));
    }

    @Test
    void createAccountReturnsForbiddenForNonSuperAdmin() throws Exception {
        mockMvc.perform(post("/api/auth/accounts")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("PLATFORM_MASTER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "tenant-manager",
                                  "email": "tenant-manager@wcms.local",
                                  "temporaryPassword": "TempPassword123!",
                                  "role": "TENANT_MASTER"
                                }
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }

    @Test
    void createAccountReturnsUnauthorizedWithoutToken() throws Exception {
        mockMvc.perform(post("/api/auth/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "platform-manager",
                                  "email": "platform-manager@wcms.local",
                                  "temporaryPassword": "TempPassword123!",
                                  "role": "PLATFORM_MASTER"
                                }
                                """))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    @Test
    void createAccountReturnsConflictWhenAccountAlreadyExists() throws Exception {
        when(authService.createAccount(any(CreateAuthAccountCommand.class)))
                .thenThrow(new DuplicateAuthAccountException("auth account already exists for username"));

        mockMvc.perform(post("/api/auth/accounts")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("SUPER_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "platform-manager",
                                  "email": "platform-manager@wcms.local",
                                  "temporaryPassword": "TempPassword123!",
                                  "role": "PLATFORM_MASTER"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error.code").value("CONFLICT"));
    }

    @Test
    void createAccountReturnsBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/auth/accounts")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("SUPER_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "",
                                  "email": "not-email",
                                  "temporaryPassword": "short",
                                  "role": "PLATFORM_MASTER"
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    private static String bearerToken(String role) throws Exception {
        AuthAccount account = AuthAccount.create(
                UUID.randomUUID(),
                "test-admin",
                "test-admin@wcms.local",
                "encoded",
                AccountRole.valueOf(role)
        );
        return "Bearer " + TestJwt.issue(account);
    }

    private static final class TestJwt {

        private TestJwt() {
        }

        static String issue(AuthAccount account) throws Exception {
            com.nimbusds.jwt.JWTClaimsSet claims = new com.nimbusds.jwt.JWTClaimsSet.Builder()
                    .issuer("wcms-test")
                    .subject(account.getId().toString())
                    .claim("username", account.getUsername())
                    .claim("role", account.getRole().name())
                    .claim("tokenVersion", account.getTokenVersion())
                    .issueTime(java.util.Date.from(Instant.parse("2026-05-20T00:00:00Z")))
                    .expirationTime(java.util.Date.from(Instant.now().plusSeconds(900)))
                    .build();
            com.nimbusds.jwt.SignedJWT jwt = new com.nimbusds.jwt.SignedJWT(
                    new com.nimbusds.jose.JWSHeader(com.nimbusds.jose.JWSAlgorithm.HS256),
                    claims
            );
            jwt.sign(new com.nimbusds.jose.crypto.MACSigner("01234567890123456789012345678901"));
            return jwt.serialize();
        }
    }
}
