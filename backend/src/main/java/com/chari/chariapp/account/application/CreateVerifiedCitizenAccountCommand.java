package com.chari.chariapp.account.application;

import com.chari.chariapp.account.domain.EnrollmentChallengeId;

import java.util.Objects;

public record CreateVerifiedCitizenAccountCommand(
        EnrollmentChallengeId enrollmentChallengeId,
        String emailLookup,
        String encryptedEmail,
        String rawPassword
) {
    public CreateVerifiedCitizenAccountCommand {
        Objects.requireNonNull(enrollmentChallengeId, "Enrollment challenge ID is required");
    }
}
