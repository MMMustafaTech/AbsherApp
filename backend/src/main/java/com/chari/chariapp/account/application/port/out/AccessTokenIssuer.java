package com.chari.chariapp.account.application.port.out;

import com.chari.chariapp.account.domain.Account;

import java.time.Instant;

public interface AccessTokenIssuer {

    IssuedAccessToken issue(Account account, Instant issuedAt);

    record IssuedAccessToken(String value, Instant expiresAt) {
    }
}
