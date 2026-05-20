package com.wcms.auth.application;

import com.wcms.auth.api.AuthTokenResponse;
import com.wcms.auth.api.LoginRequest;
import com.wcms.auth.api.LogoutRequest;
import com.wcms.auth.api.RefreshTokenRequest;
import com.wcms.auth.api.RequestMeta;
import com.wcms.auth.domain.account.AuthAccount;
import com.wcms.auth.domain.token.RefreshTokenSession;
import com.wcms.auth.infra.persistence.AuthAccountRepository;
import com.wcms.auth.infra.persistence.RefreshTokenSessionRepository;
import com.wcms.auth.infra.security.JwtTokenIssuer;
import com.wcms.auth.infra.security.RefreshTokenGenerator;
import com.wcms.auth.infra.security.TokenHash;
import java.time.Clock;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthAccountRepository accountRepository;
    private final RefreshTokenSessionRepository refreshTokenSessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenIssuer jwtTokenIssuer;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final AuthTokenProperties tokenProperties;
    private final AuthLoginProperties loginProperties;
    private final Clock clock;

    public AuthService(
            AuthAccountRepository accountRepository,
            RefreshTokenSessionRepository refreshTokenSessionRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenIssuer jwtTokenIssuer,
            RefreshTokenGenerator refreshTokenGenerator,
            AuthTokenProperties tokenProperties,
            AuthLoginProperties loginProperties,
            Clock clock
    ) {
        this.accountRepository = accountRepository;
        this.refreshTokenSessionRepository = refreshTokenSessionRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenIssuer = jwtTokenIssuer;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.tokenProperties = tokenProperties;
        this.loginProperties = loginProperties;
        this.clock = clock;
    }

    @Transactional
    public AuthAccount createAccount(CreateAuthAccountCommand command) {
        if (accountRepository.existsByUsername(command.username())) {
            throw new DuplicateAuthAccountException("auth account already exists for username");
        }
        if (accountRepository.existsByEmail(command.email())) {
            throw new DuplicateAuthAccountException("auth account already exists for email");
        }

        AuthAccount account = AuthAccount.create(
                UUID.randomUUID(),
                command.username(),
                command.email(),
                passwordEncoder.encode(command.temporaryPassword()),
                command.role()
        );
        return accountRepository.save(account);
    }

    @Transactional
    public AuthTokenResponse login(LoginRequest request, RequestMeta requestMeta) {
        Instant now = clock.instant();
        AuthAccount account = accountRepository.findByUsername(request.username())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        account.unlockIfLockExpired(now);
        if (!account.canLoginAt(now) || !passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            account.recordLoginFailure(now, loginProperties.maxFailures(), loginProperties.lockDuration());
            throw new BadCredentialsException("Invalid username or password");
        }

        account.recordLoginSuccess(now);
        return issueTokenResponse(account, requestMeta, now);
    }

    @Transactional
    public AuthTokenResponse refresh(RefreshTokenRequest request, RequestMeta requestMeta) {
        Instant now = clock.instant();
        String tokenHash = TokenHash.sha256(request.refreshToken());
        RefreshTokenSession session = refreshTokenSessionRepository.findByTokenHash(tokenHash)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));

        if (!session.isActiveAt(now)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        AuthAccount account = accountRepository.findById(session.getAccountId())
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token"));
        account.unlockIfLockExpired(now);
        if (!account.canLoginAt(now)) {
            throw new BadCredentialsException("Invalid refresh token");
        }

        session.revoke(now);
        return issueTokenResponse(account, requestMeta, now);
    }

    @Transactional
    public void logout(LogoutRequest request) {
        Instant now = clock.instant();
        String tokenHash = TokenHash.sha256(request.refreshToken());
        refreshTokenSessionRepository.findByTokenHash(tokenHash)
                .filter(session -> session.isActiveAt(now))
                .ifPresent(session -> session.revoke(now));
    }

    private AuthTokenResponse issueTokenResponse(AuthAccount account, RequestMeta requestMeta, Instant now) {
        JwtTokenIssuer.IssuedAccessToken accessToken = jwtTokenIssuer.issue(account, now);
        String refreshToken = refreshTokenGenerator.generate();
        Instant refreshTokenExpiresAt = now.plus(tokenProperties.refreshTokenTtl());

        RefreshTokenSession session = RefreshTokenSession.issue(
                UUID.randomUUID(),
                account.getId(),
                TokenHash.sha256(refreshToken),
                refreshTokenExpiresAt,
                requestMeta.ipAddress(),
                requestMeta.userAgent()
        );
        refreshTokenSessionRepository.save(session);

        return new AuthTokenResponse(
                "Bearer",
                accessToken.tokenValue(),
                tokenProperties.accessTokenTtl().toSeconds(),
                accessToken.expiresAt(),
                refreshToken,
                refreshTokenExpiresAt,
                account.isPasswordChangeRequired()
        );
    }
}
