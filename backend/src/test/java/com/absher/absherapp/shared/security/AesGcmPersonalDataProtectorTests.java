package com.absher.absherapp.shared.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AesGcmPersonalDataProtectorTests {

    private final PersonalDataProtector protector = new AesGcmPersonalDataProtector(
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
            "ZmVkY2JhOTg3NjU0MzIxMGZlZGNiYTk4NzY1NDMyMTA="
    );

    @Test
    void createsStableLookupsAndRandomizedCiphertexts() {
        assertThat(protector.lookup("ABC123456"))
                .isEqualTo(protector.lookup("ABC123456"))
                .hasSize(64);
        assertThat(protector.encrypt("person@example.com"))
                .isNotEqualTo("person@example.com")
                .isNotEqualTo(protector.encrypt("person@example.com"));
    }

    @Test
    void decryptsOnlyValuesEncryptedWithTheSameKey() {
        String ciphertext = protector.encrypt("person@example.com");

        assertThat(protector.decrypt(ciphertext)).isEqualTo("person@example.com");
    }
}
