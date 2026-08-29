package com.absher.absherapp.identityrequest.domain;

import com.absher.absherapp.account.domain.AccountId;

import java.time.Instant;
import java.util.UUID;

public record NationalIdentityRequestStatusChange(
        UUID id,
        UUID requestId,
        NationalIdentityRequestStatus fromStatus,
        NationalIdentityRequestStatus toStatus,
        String reason,
        AccountId changedBy,
        Instant changedAt
) { }
