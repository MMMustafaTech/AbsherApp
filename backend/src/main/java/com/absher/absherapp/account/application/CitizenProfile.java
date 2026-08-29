package com.absher.absherapp.account.application;

import java.time.Instant;

/** Data deliberately limited to what a citizen may view about their own account. */
public record CitizenProfile(
        String email,
        String maskedNationalId,
        String maskedVerifiedPhone,
        boolean phoneVerified,
        Instant phoneVerifiedAt,
        Instant accountCreatedAt
) {
}
