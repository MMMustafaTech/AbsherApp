package com.absher.absherapp.account.infrastructure.security;

import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.account.domain.EmailReference;
import com.absher.absherapp.citizen.domain.CitizenId;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class JwtAccessTokenIssuerTests {

    @Autowired
    private JwtAccessTokenIssuer issuer;

    @Autowired
    private JwtDecoder decoder;

    @Test
    void issuesASignedJwtWithoutPersonalData() {
        Account account = new Account(
                AccountId.newId(), CitizenId.newId(), new EmailReference("a".repeat(64), "ciphertext"),
                "password-hash", AccountStatus.ACTIVE, Set.of(AccountRole.CITIZEN), Instant.parse("2026-08-28T00:00:00Z")
        );

        var issued = issuer.issue(account, Instant.now());
        Jwt decoded = decoder.decode(issued.value());

        assertThat(decoded.getSubject()).isEqualTo(account.id().value().toString());
        assertThat(decoded.getIssuer().toString()).isEqualTo("https://absher.test");
        assertThat(decoded.getClaimAsStringList("roles")).containsExactly("CITIZEN");
        assertThat(((Number) decoded.getClaim("authorization_version")).longValue()).isZero();
        assertThat(decoded.getTokenValue()).doesNotContain("ciphertext");
    }
}
