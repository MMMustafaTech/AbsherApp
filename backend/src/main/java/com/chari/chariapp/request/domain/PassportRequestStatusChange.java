package com.chari.chariapp.request.domain;

import com.chari.chariapp.account.domain.AccountId;

import java.time.Instant;
import java.util.UUID;

/** Immutable history entry; it contains no citizen personal data. */
public record PassportRequestStatusChange(
        UUID id,
        UUID requestId,
        PassportRequestStatus fromStatus,
        PassportRequestStatus toStatus,
        String reason,
        AccountId changedBy,
        Instant changedAt
) {
}
