package com.absher.absherapp.shared.security;

/**
 * Creates deterministic lookup values and randomized encrypted values for personal data.
 * Raw national IDs, phone numbers and emails must not be persisted or logged.
 */
public interface PersonalDataProtector {

    String lookup(String normalizedValue);

    String encrypt(String plaintext);

    String decrypt(String ciphertext);
}
