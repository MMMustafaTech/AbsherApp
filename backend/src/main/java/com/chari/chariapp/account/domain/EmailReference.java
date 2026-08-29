package com.chari.chariapp.account.domain;

import java.util.Objects;

public record EmailReference(String lookup, String ciphertext) {

    public EmailReference {
        Objects.requireNonNull(lookup, "Email lookup is required");
        Objects.requireNonNull(ciphertext, "Encrypted email is required");

        if (!lookup.matches("[a-fA-F0-9]{64}")) {
            throw new IllegalArgumentException("Email lookup must be a SHA-256-sized hexadecimal value");
        }
        if (ciphertext.isBlank()) {
            throw new IllegalArgumentException("Encrypted email must not be blank");
        }
    }
}
