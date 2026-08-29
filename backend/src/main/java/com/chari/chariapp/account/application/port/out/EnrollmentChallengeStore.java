package com.chari.chariapp.account.application.port.out;

import com.chari.chariapp.account.domain.EnrollmentChallenge;
import com.chari.chariapp.account.domain.EnrollmentChallengeId;

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
