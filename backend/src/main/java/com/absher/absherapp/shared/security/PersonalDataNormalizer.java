package com.absher.absherapp.shared.security;

import java.util.Locale;

/** Normalization shared by request handling and one-off legacy migrations. */
public final class PersonalDataNormalizer {

    private PersonalDataNormalizer() {
    }

    public static String nationalId(String value) {
        if (value == null) {
            throw new IllegalArgumentException("National ID is required");
        }
        String normalized = value.replaceAll("[\\s-]", "").toUpperCase(Locale.ROOT);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("National ID is required");
        }
        return normalized;
    }

    public static String phone(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Phone number is required");
        }
        String normalized = value.replaceAll("[\\s()-]", "");
        if (!normalized.matches("\\+[1-9]\\d{7,14}")) {
            throw new IllegalArgumentException("Phone number must be in E.164 format");
        }
        return normalized;
    }
}
