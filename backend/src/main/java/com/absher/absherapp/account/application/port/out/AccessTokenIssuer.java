package com.absher.absherapp.account.application.port.out;

import com.absher.absherapp.account.domain.Account;

import java.time.Instant;

public interface AccessTokenIssuer {

    IssuedAccessToken issue(Account account, Instant issuedAt);

    record IssuedAccessToken(String value, Instant expiresAt) {
    }
}
