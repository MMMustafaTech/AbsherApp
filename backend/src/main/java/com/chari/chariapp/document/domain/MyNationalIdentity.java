package com.chari.chariapp.document.domain;

import java.time.LocalDate;

public record MyNationalIdentity(
        String nationalId, String firstName, String lastName, String gender, String placeOfBirth,
        LocalDate dateOfBirth, String cardSerial, String placeOfIssue, LocalDate issuedOn,
        LocalDate expiresOn, String profession, String fatherName, String motherName,
        String address, String bloodGroup
) {
}
