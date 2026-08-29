package com.absher.absherapp.account.domain;

import java.util.Objects;
import java.util.UUID;

public record EnrollmentChallengeId(UUID value) {

    public EnrollmentChallengeId {
        Objects.requireNonNull(value, "Enrollment challenge ID is required");
    }

    public static EnrollmentChallengeId newId() {
        return new EnrollmentChallengeId(UUID.randomUUID());
    }
}
