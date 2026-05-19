package com.wcms.auth.infra.security;

import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.wcms.auth.application.AuthTokenProperties;
import com.wcms.core.security.AuthenticatedUser;
import java.text.ParseException;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

@Component
public class JwtTokenVerifier {

    private final AuthTokenProperties tokenProperties;
    private final Clock clock;

    public JwtTokenVerifier(AuthTokenProperties tokenProperties, Clock clock) {
        this.tokenProperties = tokenProperties;
        this.clock = clock;
    }

    public AuthenticatedUser verify(String tokenValue) {
        try {
            SignedJWT jwt = SignedJWT.parse(tokenValue);
            if (!JWSAlgorithm.HS256.equals(jwt.getHeader().getAlgorithm())) {
                throw invalidToken();
            }
            if (!jwt.verify(new MACVerifier(tokenProperties.secret()))) {
                throw invalidToken();
            }

            JWTClaimsSet claims = jwt.getJWTClaimsSet();
            validateRegisteredClaims(claims);
            return new AuthenticatedUser(
                    UUID.fromString(claims.getSubject()),
                    claims.getStringClaim("username"),
                    claims.getStringClaim("role"),
                    claims.getLongClaim("tokenVersion")
            );
        } catch (ParseException | JOSEException | IllegalArgumentException exception) {
            throw invalidToken();
        }
    }

    private void validateRegisteredClaims(JWTClaimsSet claims) throws ParseException {
        Instant now = clock.instant();
        Date expiresAt = claims.getExpirationTime();
        if (!tokenProperties.issuer().equals(claims.getIssuer())) {
            throw invalidToken();
        }
        if (claims.getSubject() == null || claims.getSubject().isBlank()) {
            throw invalidToken();
        }
        if (expiresAt == null || !expiresAt.toInstant().isAfter(now)) {
            throw invalidToken();
        }
        if (claims.getStringClaim("username") == null || claims.getStringClaim("username").isBlank()) {
            throw invalidToken();
        }
        if (claims.getStringClaim("role") == null || claims.getStringClaim("role").isBlank()) {
            throw invalidToken();
        }
        if (claims.getLongClaim("tokenVersion") == null) {
            throw invalidToken();
        }
    }

    private BadCredentialsException invalidToken() {
        return new BadCredentialsException("Invalid access token");
    }
}
