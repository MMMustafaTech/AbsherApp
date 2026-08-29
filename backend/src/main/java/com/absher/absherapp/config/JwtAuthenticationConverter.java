package com.absher.absherapp.config;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.InvalidBearerTokenException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class JwtAuthenticationConverter implements Converter<Jwt, AbstractAuthenticationToken> {

    private final AccountStore accountStore;

    public JwtAuthenticationConverter(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    @Override
    public AbstractAuthenticationToken convert(Jwt jwt) {
        AccountId accountId;
        try {
            accountId = new AccountId(UUID.fromString(jwt.getSubject()));
        } catch (IllegalArgumentException ex) {
            throw new InvalidBearerTokenException("Token subject is invalid", ex);
        }
        long tokenAuthorizationVersion = authorizationVersion(jwt);
        var account = accountStore.findById(accountId)
                .filter(candidate -> candidate.status() == AccountStatus.ACTIVE)
                .filter(candidate -> candidate.authorizationVersion() == tokenAuthorizationVersion)
                .orElseThrow(() -> new InvalidBearerTokenException("Token is no longer authorized"));
        List<SimpleGrantedAuthority> authorities = account.roles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                .toList();
        return new JwtAuthenticationToken(jwt, authorities, jwt.getSubject());
    }

    private static long authorizationVersion(Jwt jwt) {
        Number value = jwt.getClaim("authorization_version");
        if (value == null || value.longValue() < 0) {
            throw new InvalidBearerTokenException("Token authorization version is invalid");
        }
        return value.longValue();
    }
}
