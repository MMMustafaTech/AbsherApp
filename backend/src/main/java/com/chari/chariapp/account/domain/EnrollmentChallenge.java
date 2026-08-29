package com.chari.chariapp.account.domain;

import com.chari.chariapp.citizen.domain.CitizenId;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public record EnrollmentChallenge(
        EnrollmentChallengeId id,
        CitizenId citizenId,
        VerificationChallengePurpose purpose,
        String destinationLookup,
        String destinationCiphertext,
        String codeHash,
        Instant expiresAt,
        int failedAttempts,
        Instant verifiedAt,
        Instant enrollmentConsumedAt
) {
    public static final int MAX_FAILED_ATTEMPTS = 5;

    public EnrollmentChallenge {
        Objects.requireNonNull(id, "Challenge ID is required");
        Objects.requireNonNull(citizenId, "Citizen ID is required");
        Objects.requireNonNull(purpose, "Challenge purpose is required");
        Objects.requireNonNull(destinationLookup, "Destination lookup is required");
        Objects.requireNonNull(codeHash, "Code hash is required");
        Objects.requireNonNull(expiresAt, "Expiry time is required");
    }

    public boolean isExpired(Instant now) {
        return !expiresAt.isAfter(now);
    }

    public boolean isLocked() {
        return failedAttempts >= MAX_FAILED_ATTEMPTS;
    }

    public boolean isVerified() {
        return verifiedAt != null;
    }

    public boolean isEnrollmentConsumed() {
        return enrollmentConsumedAt != null;
    }

    public Optional<String> destinationCiphertextOptional() {
        return Optional.ofNullable(destinationCiphertext);
    }

    public EnrollmentChallenge registerFailedAttempt() {
        return new EnrollmentChallenge(
                id, citizenId, purpose, destinationLookup, destinationCiphertext, codeHash,
                expiresAt, failedAttempts + 1, verifiedAt, enrollmentConsumedAt
        );
    }

    public EnrollmentChallenge markVerified(Instant verifiedAt) {
        return new EnrollmentChallenge(
                id, citizenId, purpose, destinationLookup, destinationCiphertext, codeHash,
                expiresAt, failedAttempts, verifiedAt, enrollmentConsumedAt
        );
    }

    public EnrollmentChallenge markEnrollmentConsumed(Instant consumedAt) {
        return new EnrollmentChallenge(
                id, citizenId, purpose, destinationLookup, destinationCiphertext, codeHash,
                expiresAt, failedAttempts, verifiedAt, consumedAt
        );
    }
}
