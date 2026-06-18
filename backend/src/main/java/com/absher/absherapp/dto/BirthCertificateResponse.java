package com.absher.absherapp.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BirthCertificateResponse {

    private String certificateNumber;

    private String fullName;
    private String gender;
    private String birthDate;
    private String birthPlace;

    private String fatherName;
    private String fatherBirthDate;
    private String fatherBirthPlace;
    private String fatherProfession;

    private String motherName;
    private String motherBirthDate;
    private String motherBirthPlace;
    private String motherProfession;

    private String declarationDate;
    private String address;
}