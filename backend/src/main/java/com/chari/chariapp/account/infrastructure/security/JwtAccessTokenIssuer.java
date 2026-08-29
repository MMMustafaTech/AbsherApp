package com.chari.chariapp.account.infrastructure.security;

import com.chari.chariapp.account.application.port.out.AccessTokenIssuer;
import com.chari.chariapp.account.domain.Account;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
public class JwtAccessTokenIssuer implements AccessTokenIssuer {

    private final JwtEncoder jwtEncoder;
    private final String issuer;
    private final Duration accessTokenLifetime;

    public JwtAccessTokenIssuer(
            JwtEncoder jwtEncoder,
            @Value("${app.jwt.issuer}") String issuer,
            @Value("${app.jwt.access-token-lifetime}") Duration accessTokenLifetime
    ) {
        this.jwtEncoder = jwtEncoder;
        this.issuer = issuer;
        this.accessTokenLifetime = accessTokenLifetime;
    }

    @Override
    public IssuedAccessToken issue(Account account, Instant issuedAt) {
        Instant expiresAt = issuedAt.plus(accessTokenLifetime);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer(issuer)
                .subject(account.id().value().toString())
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .claim("roles", account.roles().stream().map(Enum::name).sorted().toList())
                .claim("authorization_version", account.authorizationVersion())
                .build();
        String value = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
        return new IssuedAccessToken(value, expiresAt);
    }
}
