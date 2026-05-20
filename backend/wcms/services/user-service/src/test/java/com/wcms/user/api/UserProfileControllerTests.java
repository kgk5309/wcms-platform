package com.wcms.user.api;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.wcms.user.application.CreateUserProfileCommand;
import com.wcms.user.application.DuplicateUserProfileException;
import com.wcms.user.application.UserProfileService;
import com.wcms.user.domain.UserProfile;
import com.wcms.user.domain.UserRole;
import com.wcms.user.domain.UserScopeType;
import java.time.Instant;
import java.util.Date;
import java.util.List;
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
        "spring.datasource.url=jdbc:h2:mem:user-api-test;MODE=MariaDB;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "wcms.user.token.issuer=wcms-platform",
        "wcms.user.token.secret=wcms-local-dev-secret-must-be-rotated"
})
@AutoConfigureMockMvc
class UserProfileControllerTests {

    private static final String TOKEN_SECRET = "wcms-local-dev-secret-must-be-rotated";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserProfileService userProfileService;

    @Test
    void createReturnsCreatedProfileForSuperAdmin() throws Exception {
        UUID authAccountId = UUID.randomUUID();
        UserProfile profile = UserProfile.create(
                UUID.randomUUID(),
                authAccountId,
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.SUPER_ADMIN,
                UserScopeType.PLATFORM,
                null,
                null
        );
        when(userProfileService.create(any(CreateUserProfileCommand.class))).thenReturn(profile);

        mockMvc.perform(post("/api/users/profiles")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("SUPER_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "authAccountId": "%s",
                                  "displayName": "Platform Admin",
                                  "email": "platform-admin@wcms.local",
                                  "role": "SUPER_ADMIN",
                                  "scopeType": "PLATFORM"
                                }
                                """.formatted(authAccountId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.succeeded").value(true))
                .andExpect(jsonPath("$.data.authAccountId").value(authAccountId.toString()))
                .andExpect(jsonPath("$.data.email").value("platform-admin@wcms.local"));
    }

    @Test
    void createReturnsForbiddenForNonSuperAdmin() throws Exception {
        mockMvc.perform(post("/api/users/profiles")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("TENANT_MASTER"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "authAccountId": "%s",
                                  "displayName": "Tenant Admin",
                                  "email": "tenant-admin@wcms.local",
                                  "role": "TENANT_MASTER",
                                  "scopeType": "TENANT",
                                  "tenantId": "%s"
                                }
                                """.formatted(UUID.randomUUID(), UUID.randomUUID())))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.succeeded").value(false))
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }

    @Test
    void createReturnsConflictWhenProfileAlreadyExists() throws Exception {
        when(userProfileService.create(any(CreateUserProfileCommand.class)))
                .thenThrow(new DuplicateUserProfileException("user profile already exists for email"));

        mockMvc.perform(post("/api/users/profiles")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("SUPER_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "authAccountId": "%s",
                                  "displayName": "Platform Admin",
                                  "email": "platform-admin@wcms.local",
                                  "role": "SUPER_ADMIN",
                                  "scopeType": "PLATFORM"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.succeeded").value(false))
                .andExpect(jsonPath("$.error.code").value("CONFLICT"));
    }

    @Test
    void createReturnsBadRequestWhenRequestIsInvalid() throws Exception {
        mockMvc.perform(post("/api/users/profiles")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("SUPER_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "authAccountId": "%s",
                                  "displayName": "",
                                  "email": "not-email",
                                  "role": "SUPER_ADMIN",
                                  "scopeType": "PLATFORM"
                                }
                                """.formatted(UUID.randomUUID())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.succeeded").value(false))
                .andExpect(jsonPath("$.error.code").value("INVALID_REQUEST"));
    }

    @Test
    void getReturnsProfileForSuperAdmin() throws Exception {
        UUID profileId = UUID.randomUUID();
        UUID authAccountId = UUID.randomUUID();
        when(userProfileService.get(profileId)).thenReturn(UserProfile.create(
                profileId,
                authAccountId,
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.SUPER_ADMIN,
                UserScopeType.PLATFORM,
                null,
                null
        ));

        mockMvc.perform(get("/api/users/profiles/{id}", profileId)
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("SUPER_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").value(profileId.toString()))
                .andExpect(jsonPath("$.data.authAccountId").value(authAccountId.toString()));
    }

    @Test
    void findAllReturnsProfilesForSuperAdmin() throws Exception {
        when(userProfileService.findAll()).thenReturn(List.of(UserProfile.create(
                UUID.randomUUID(),
                UUID.randomUUID(),
                "Platform Admin",
                "platform-admin@wcms.local",
                null,
                UserRole.SUPER_ADMIN,
                UserScopeType.PLATFORM,
                null,
                null
        )));

        mockMvc.perform(get("/api/users/profiles")
                        .header(HttpHeaders.AUTHORIZATION, bearerToken("SUPER_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(1))
                .andExpect(jsonPath("$.data[0].email").value("platform-admin@wcms.local"));
    }

    @Test
    void requestWithoutTokenReturnsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/users/profiles"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.succeeded").value(false))
                .andExpect(jsonPath("$.error.code").value("UNAUTHORIZED"));
    }

    private static String bearerToken(String role) throws Exception {
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer("wcms-platform")
                .subject(UUID.randomUUID().toString())
                .claim("username", "platform-admin")
                .claim("role", role)
                .claim("tokenVersion", 0L)
                .issueTime(Date.from(Instant.parse("2026-05-20T00:00:00Z")))
                .expirationTime(Date.from(Instant.now().plusSeconds(900)))
                .build();
        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        jwt.sign(new MACSigner(TOKEN_SECRET));
        return "Bearer " + jwt.serialize();
    }
}
