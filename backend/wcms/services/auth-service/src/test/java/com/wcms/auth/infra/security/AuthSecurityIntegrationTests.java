package com.wcms.auth.infra.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.wcms.auth.domain.account.AccountRole;
import com.wcms.auth.domain.account.AuthAccount;
import java.security.Principal;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:auth-security;MODE=MariaDB;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=none",
        "wcms.auth.token.issuer=wcms-test",
        "wcms.auth.token.secret=01234567890123456789012345678901"
})
@AutoConfigureMockMvc
@Import(AuthSecurityIntegrationTests.ProtectedController.class)
class AuthSecurityIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenIssuer jwtTokenIssuer;

    @Test
    void validAccessTokenCanAccessProtectedEndpoint() throws Exception {
        AuthAccount account = AuthAccount.create(
                UUID.fromString("00000000-0000-4000-8000-000000000001"),
                "platform-admin",
                "platform-admin@wcms.local",
                "encoded",
                AccountRole.SUPER_ADMIN
        );
        String token = jwtTokenIssuer.issue(account, Instant.now()).tokenValue();

        mockMvc.perform(get("/test/protected/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("platform-admin"))
                .andExpect(jsonPath("$.role").value("SUPER_ADMIN"));
    }

    @Test
    void missingAccessTokenCannotAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/test/protected/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.error.message").value("Authentication required"));
    }

    @Test
    void malformedAccessTokenCannotAccessProtectedEndpoint() throws Exception {
        mockMvc.perform(get("/test/protected/me")
                        .header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"))
                .andExpect(jsonPath("$.error.message").value("Invalid access token"));
    }

    @RestController
    static class ProtectedController {

        @GetMapping("/test/protected/me")
        AuthPrincipal me(Principal principal) {
            return (AuthPrincipal) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        }
    }
}
