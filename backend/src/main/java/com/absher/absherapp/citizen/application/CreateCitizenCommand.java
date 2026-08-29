package com.absher.absherapp.citizen.application;

public record CreateCitizenCommand(
        String nationalIdLookup,
        String encryptedNationalId,
        String phoneLookup,
        String encryptedPhone,
        java.time.Instant phoneVerifiedAt
) {
}
