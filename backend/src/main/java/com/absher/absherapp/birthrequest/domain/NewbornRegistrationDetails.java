package com.absher.absherapp.birthrequest.domain;

import java.time.LocalDate;
import java.util.Objects;

/** Sensitive information supplied only when registering a newborn. */
public record NewbornRegistrationDetails(
        String childFirstName,
        String childLastName,
        LocalDate dateOfBirth,
        String placeOfBirth,
        NewbornGender gender,
        String fatherFullName,
        String fatherNationalId,
        String motherFullName,
        String motherNationalId
) {
    public NewbornRegistrationDetails {
        childFirstName = requiredText(childFirstName, "Child first name", 100);
        childLastName = requiredText(childLastName, "Child last name", 100);
        Objects.requireNonNull(dateOfBirth, "Date of birth is required");
        if (dateOfBirth.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Date of birth cannot be in the future");
        }
        placeOfBirth = requiredText(placeOfBirth, "Place of birth", 200);
        Objects.requireNonNull(gender, "Gender is required");
        fatherFullName = requiredText(fatherFullName, "Father full name", 200);
        fatherNationalId = nationalId(fatherNationalId, "Father national ID");
        motherFullName = requiredText(motherFullName, "Mother full name", 200);
        motherNationalId = nationalId(motherNationalId, "Mother national ID");
    }

    private static String requiredText(String value, String fieldName, int maximumLength) {
        String normalized = value == null ? null : value.trim();
        if (normalized == null || normalized.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        if (normalized.length() > maximumLength) {
            throw new IllegalArgumentException(fieldName + " must not exceed " + maximumLength + " characters");
        }
        return normalized;
    }

    private static String nationalId(String value, String fieldName) {
        String normalized = requiredText(value, fieldName, 32);
        if (!normalized.matches("[0-9]{5,32}")) {
            throw new IllegalArgumentException(fieldName + " must contain 5 to 32 digits");
        }
        return normalized;
    }
}
