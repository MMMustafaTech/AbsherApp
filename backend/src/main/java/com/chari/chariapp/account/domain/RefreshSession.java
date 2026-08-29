package com.chari.chariapp.account.domain;

import java.time.Instant;
import java.util.Objects;

public record RefreshSession(
        RefreshSessionId id,
        AccountId accountId,
        String tokenHash,
        Instant createdAt,
        Instant expiresAt,
        Instant revokedAt
) {
    public RefreshSession {
        Objects.requireNonNull(id, "Refresh session ID is required");
        Objects.requireNonNull(accountId, "Account ID is required");
        Objects.requireNonNull(tokenHash, "Refresh token hash is required");
        Objects.requireNonNull(createdAt, "Creation time is required");
        Objects.requireNonNull(expiresAt, "Expiry time is required");
        if (!expiresAt.isAfter(createdAt)) {
            throw new IllegalArgumentException("Refresh session must expire after it is created");
        }
    }

    public boolean isUsable(Instant now) {
        return revokedAt == null && expiresAt.isAfter(now);
    }

    public RefreshSession revoke(Instant revokedAt) {
        return new RefreshSession(id, accountId, tokenHash, createdAt, expiresAt, revokedAt);
    }
}
