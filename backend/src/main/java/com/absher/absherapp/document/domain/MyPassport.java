package com.absher.absherapp.document.domain;

import java.time.LocalDate;

public record MyPassport(
        String passportNumber, String firstName, String lastName, LocalDate dateOfBirth,
        String placeOfBirth, LocalDate issuedOn, LocalDate expiresOn, String placeOfIssue,
        String issuingAuthority, String profession, String nationality, String sex
) {
}
