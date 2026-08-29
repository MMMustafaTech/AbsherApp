package com.chari.chariapp.citizen.application;

public record CreateCitizenCommand(
        String nationalIdLookup,
        String encryptedNationalId,
        String phoneLookup,
        String encryptedPhone,
        java.time.Instant phoneVerifiedAt
) {
}
