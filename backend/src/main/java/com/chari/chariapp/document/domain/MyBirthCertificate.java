package com.chari.chariapp.document.domain;

import java.time.LocalDate;

public record MyBirthCertificate(
        String certificateNumber, String fullName, String gender, LocalDate birthDate, String birthPlace,
        String fatherName, LocalDate fatherBirthDate, String fatherBirthPlace, String fatherProfession,
        String motherName, LocalDate motherBirthDate, String motherBirthPlace, String motherProfession,
        LocalDate declarationDate, String address
) {
}
