package com.wcms.auth.infra.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.wcms.auth.application.AuthTokenProperties;
import com.wcms.auth.domain.account.AuthAccount;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenIssuer {

    private final AuthTokenProperties tokenProperties;

    public JwtTokenIssuer(AuthTokenProperties tokenProperties) {
        this.tokenProperties = tokenProperties;
    }

    public IssuedAccessToken issue(AuthAccount account, Instant issuedAt) {
        Instant expiresAt = issuedAt.plus(tokenProperties.accessTokenTtl());
        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .issuer(tokenProperties.issuer())
                .subject(account.getId().toString())
                .jwtID(UUID.randomUUID().toString())
                .issueTime(Date.from(issuedAt))
                .expirationTime(Date.from(expiresAt))
                .claim("username", account.getUsername())
                .claim("role", account.getRole().name())
                .claim("tokenVersion", account.getTokenVersion())
                .build();

        SignedJWT jwt = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claims);
        try {
            jwt.sign(new MACSigner(tokenProperties.secret()));
        } catch (JOSEException e) {
            throw new IllegalStateException("Failed to sign access token", e);
        }

        return new IssuedAccessToken(jwt.serialize(), expiresAt);
    }

    public record IssuedAccessToken(
            String tokenValue,
            Instant expiresAt
    ) {
    }
}
