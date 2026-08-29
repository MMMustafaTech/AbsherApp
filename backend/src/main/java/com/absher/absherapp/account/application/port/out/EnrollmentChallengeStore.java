package com.absher.absherapp.account.application.port.out;

import com.absher.absherapp.account.domain.EnrollmentChallenge;
import com.absher.absherapp.account.domain.EnrollmentChallengeId;

import java.time.Instant;

import java.util.Optional;

public interface EnrollmentChallengeStore {

    EnrollmentChallenge save(EnrollmentChallenge challenge);

    Optional<EnrollmentChallenge> findById(EnrollmentChallengeId id);

    /**
     * Atomically marks a verified, unexpired challenge as consumed. A challenge is a one-time enrollment proof.
     */
    boolean consumeVerified(EnrollmentChallengeId id, Instant consumedAt);
}
