package com.absher.absherapp.citizen.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

public record Citizen(
        CitizenId id,
        NationalIdReference nationalId,
        PhoneReference verifiedPhone,
        Instant phoneVerifiedAt,
        Instant createdAt
) {

    public Citizen {
        Objects.requireNonNull(id, "Citizen ID is required");
        Objects.requireNonNull(nationalId, "National ID reference is required");
        Objects.requireNonNull(createdAt, "Creation time is required");
        if ((verifiedPhone == null) != (phoneVerifiedAt == null)) {
            throw new IllegalArgumentException("A verified phone and its verification time must be stored together");
        }
    }

    /** A migrated citizen may not have supplied a verified phone yet. */
    public Optional<PhoneReference> verifiedPhoneOptional() {
        return Optional.ofNullable(verifiedPhone);
    }
}
