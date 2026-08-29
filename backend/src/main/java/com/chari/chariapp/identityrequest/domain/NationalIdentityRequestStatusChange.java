package com.chari.chariapp.identityrequest.domain;

import com.chari.chariapp.account.domain.AccountId;

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
