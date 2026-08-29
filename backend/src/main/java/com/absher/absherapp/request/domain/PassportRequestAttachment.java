package com.absher.absherapp.request.domain;

import com.absher.absherapp.account.domain.AccountId;

import java.time.Instant;
import java.util.UUID;

/** Immutable metadata for a file attached to a passport service request. */
public record PassportRequestAttachment(
        UUID id,
        UUID requestId,
        String storageKey,
        String originalFileName,
        String contentType,
        long sizeBytes,
        AccountId uploadedBy,
        Instant uploadedAt
) {
    public PassportRequestAttachment {
        if (sizeBytes < 1) {
            throw new IllegalArgumentException("Attachment must not be empty");
        }
    }
}
