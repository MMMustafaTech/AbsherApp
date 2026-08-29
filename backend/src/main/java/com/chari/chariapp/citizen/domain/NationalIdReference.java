package com.chari.chariapp.citizen.domain;

import java.util.Objects;

public record NationalIdReference(String lookup, String ciphertext) {

    public NationalIdReference {
        Objects.requireNonNull(lookup, "National ID lookup is required");
        Objects.requireNonNull(ciphertext, "Encrypted national ID is required");

        if (!lookup.matches("[a-fA-F0-9]{64}")) {
            throw new IllegalArgumentException("National ID lookup must be a SHA-256-sized hexadecimal value");
        }
        if (ciphertext.isBlank()) {
            throw new IllegalArgumentException("Encrypted national ID must not be blank");
        }
    }
}
