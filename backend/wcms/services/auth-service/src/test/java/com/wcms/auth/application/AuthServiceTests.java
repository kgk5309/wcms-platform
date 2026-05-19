package com.wcms.auth.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.wcms.auth.api.LoginRequest;
import com.wcms.auth.api.LogoutRequest;
import com.wcms.auth.api.RefreshTokenRequest;
import com.wcms.auth.api.RequestMeta;
import com.wcms.auth.domain.account.AccountRole;
import com.wcms.auth.domain.account.AuthAccount;
import com.wcms.auth.domain.token.RefreshTokenSession;
import com.wcms.auth.infra.persistence.AuthAccountRepository;
import com.wcms.auth.infra.persistence.RefreshTokenSessionRepository;
import com.wcms.auth.infra.security.JwtTokenIssuer;
import com.wcms.auth.infra.security.RefreshTokenGenerator;
import com.wcms.auth.infra.security.TokenHash;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

class AuthServiceTests {

    private static final Instant NOW = Instant.parse("2026-05-19T00:00:00Z");
    private static final RequestMeta REQUEST_META = new RequestMeta("127.0.0.1", "JUnit");

    private final AuthAccountRepository accountRepository = org.mockito.Mockito.mock(AuthAccountRepository.class);
    private final RefreshTokenSessionRepository refreshTokenSessionRepository =
            org.mockito.Mockito.mock(RefreshTokenSessionRepository.class);
    private final PasswordEncoder passwordEncoder = org.mockito.Mockito.mock(PasswordEncoder.class);
    private final JwtTokenIssuer jwtTokenIssuer = org.mockito.Mockito.mock(JwtTokenIssuer.class);
    private final RefreshTokenGenerator refreshTokenGenerator = org.mockito.Mockito.mock(RefreshTokenGenerator.class);
    private final AuthTokenProperties tokenProperties = new AuthTokenProperties(
            "wcms-test",
            "01234567890123456789012345678901",
            Duration.ofMinutes(15),
            Duration.ofDays(14)
    );
    private final AuthLoginProperties loginProperties = new AuthLoginProperties(5, Duration.ofMinutes(10));
    private final Clock clock = Clock.fixed(NOW, ZoneOffset.UTC);

    private final AuthService authService = new AuthService(
            accountRepository,
            refreshTokenSessionRepository,
            passwordEncoder,
            jwtTokenIssuer,
            refreshTokenGenerator,
            tokenProperties,
            loginProperties,
            clock
    );

    @Test
    void loginIssuesAccessTokenAndStoresHashedRefreshToken() {
        AuthAccount account = AuthAccount.create(
                UUID.randomUUID(),
                "admin",
                "admin@example.com",
                "encoded",
                AccountRole.PLATFORM_MASTER
        );
        when(accountRepository.findByUsername("admin")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password", "encoded")).thenReturn(true);
        when(jwtTokenIssuer.issue(account, NOW))
                .thenReturn(new JwtTokenIssuer.IssuedAccessToken("access-token", NOW.plus(Duration.ofMinutes(15))));
        when(refreshTokenGenerator.generate()).thenReturn("refresh-token");

        var response = authService.login(new LoginRequest("admin", "password"), REQUEST_META);

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.refreshTokenExpiresAt()).isEqualTo(NOW.plus(Duration.ofDays(14)));

        ArgumentCaptor<RefreshTokenSession> captor = ArgumentCaptor.forClass(RefreshTokenSession.class);
        verify(refreshTokenSessionRepository).save(captor.capture());
        assertThat(captor.getValue().getAccountId()).isEqualTo(account.getId());
        assertThat(captor.getValue().getTokenHash()).isEqualTo(TokenHash.sha256("refresh-token"));
    }

    @Test
    void refreshRevokesExistingSessionAndRotatesRefreshToken() {
        AuthAccount account = AuthAccount.create(
                UUID.randomUUID(),
                "admin",
                "admin@example.com",
                "encoded",
                AccountRole.PLATFORM_MASTER
        );
        RefreshTokenSession session = RefreshTokenSession.issue(
                UUID.randomUUID(),
                account.getId(),
                TokenHash.sha256("old-refresh-token"),
                NOW.plus(Duration.ofDays(1)),
                "127.0.0.1",
                "JUnit"
        );
        when(refreshTokenSessionRepository.findByTokenHash(TokenHash.sha256("old-refresh-token")))
                .thenReturn(Optional.of(session));
        when(accountRepository.findById(account.getId())).thenReturn(Optional.of(account));
        when(jwtTokenIssuer.issue(account, NOW))
                .thenReturn(new JwtTokenIssuer.IssuedAccessToken("new-access-token", NOW.plus(Duration.ofMinutes(15))));
        when(refreshTokenGenerator.generate()).thenReturn("new-refresh-token");

        var response = authService.refresh(new RefreshTokenRequest("old-refresh-token"), REQUEST_META);

        assertThat(session.getRevokedAt()).isEqualTo(NOW);
        assertThat(response.accessToken()).isEqualTo("new-access-token");
        assertThat(response.refreshToken()).isEqualTo("new-refresh-token");
        verify(refreshTokenSessionRepository).save(any(RefreshTokenSession.class));
    }

    @Test
    void refreshRejectsAlreadyRevokedRefreshToken() {
        RefreshTokenSession session = RefreshTokenSession.issue(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TokenHash.sha256("old-refresh-token"),
                NOW.plus(Duration.ofDays(1)),
                "127.0.0.1",
                "JUnit"
        );
        session.revoke(NOW.minusSeconds(1));
        when(refreshTokenSessionRepository.findByTokenHash(TokenHash.sha256("old-refresh-token")))
                .thenReturn(Optional.of(session));

        assertThatThrownBy(() -> authService.refresh(new RefreshTokenRequest("old-refresh-token"), REQUEST_META))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void refreshRejectsExpiredRefreshToken() {
        RefreshTokenSession session = RefreshTokenSession.issue(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TokenHash.sha256("expired-refresh-token"),
                NOW.minusSeconds(1),
                "127.0.0.1",
                "JUnit"
        );
        when(refreshTokenSessionRepository.findByTokenHash(TokenHash.sha256("expired-refresh-token")))
                .thenReturn(Optional.of(session));

        assertThatThrownBy(() -> authService.refresh(new RefreshTokenRequest("expired-refresh-token"), REQUEST_META))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid refresh token");
    }

    @Test
    void logoutRevokesActiveRefreshToken() {
        RefreshTokenSession session = RefreshTokenSession.issue(
                UUID.randomUUID(),
                UUID.randomUUID(),
                TokenHash.sha256("refresh-token"),
                NOW.plus(Duration.ofDays(1)),
                "127.0.0.1",
                "JUnit"
        );
        when(refreshTokenSessionRepository.findByTokenHash(TokenHash.sha256("refresh-token")))
                .thenReturn(Optional.of(session));

        authService.logout(new LogoutRequest("refresh-token"));

        assertThat(session.getRevokedAt()).isEqualTo(NOW);
    }

    @Test
    void logoutIgnoresUnknownRefreshToken() {
        when(refreshTokenSessionRepository.findByTokenHash(TokenHash.sha256("unknown-refresh-token")))
                .thenReturn(Optional.empty());

        authService.logout(new LogoutRequest("unknown-refresh-token"));
    }

    @Test
    void loginRecordsFailureAndLocksAccountWhenCredentialsAreInvalid() {
        AuthAccount account = AuthAccount.create(
                UUID.randomUUID(),
                "admin",
                "admin@example.com",
                "encoded",
                AccountRole.PLATFORM_MASTER
        );
        when(accountRepository.findByUsername("admin")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("wrong-password", "encoded")).thenReturn(false);

        for (int attempt = 0; attempt < loginProperties.maxFailures(); attempt += 1) {
            assertThatThrownBy(() -> authService.login(new LoginRequest("admin", "wrong-password"), REQUEST_META))
                    .isInstanceOf(BadCredentialsException.class)
                    .hasMessage("Invalid username or password");
        }

        assertThat(account.getFailedLoginCount()).isEqualTo(loginProperties.maxFailures());
        assertThat(account.getLockedUntil()).isEqualTo(NOW.plus(loginProperties.lockDuration()));
    }
}
