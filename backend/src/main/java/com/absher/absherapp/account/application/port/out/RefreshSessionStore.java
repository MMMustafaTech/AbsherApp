package com.absher.absherapp.account.application.port.out;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.RefreshSession;

import java.time.Instant;
import java.util.Optional;

public interface RefreshSessionStore {

    RefreshSession save(RefreshSession session);

    /** Returns a session under a write lock so a refresh token can only be rotated once. */
    Optional<RefreshSession> findByTokenHashForUpdate(String tokenHash);

    /** Revokes all usable refresh sessions held by one account. */
    default int revokeAllForAccount(AccountId accountId, Instant revokedAt) {
        throw new UnsupportedOperationException("Bulk refresh-session revocation is not supported");
    }
}
