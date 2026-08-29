package com.absher.absherapp.account.application;

import com.absher.absherapp.account.domain.EnrollmentChallengeId;

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
