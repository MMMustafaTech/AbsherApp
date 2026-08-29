package com.chari.chariapp.citizen.domain;

import java.util.Objects;

public record PhoneReference(String lookup, String ciphertext) {

    public PhoneReference {
        Objects.requireNonNull(lookup, "Phone lookup is required");
        Objects.requireNonNull(ciphertext, "Encrypted phone number is required");

        if (!lookup.matches("[a-fA-F0-9]{64}")) {
            throw new IllegalArgumentException("Phone lookup must be a SHA-256-sized hexadecimal value");
        }
        if (ciphertext.isBlank()) {
            throw new IllegalArgumentException("Encrypted phone number must not be blank");
        }
    }
}
