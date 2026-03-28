package com.absher.absherapp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PassportResponse {
    private String passportNumber;
    private String firstName;
    private String fatherName;
    private String lastName;

    private String birthDate;
    private String birthPlace;

    private String issueDate;
    private String expiryDate;

    private String issuePlace;
    private String issueingAuthority;

    private String profession;
    private String nationality;
    private String gender;

    public PassportResponse( String passportNumber,
                             String firstName,
                             String fatherName,
                             String lastName,
                             String birthDate,
                             String birthPlace,
                             String issueDate,
                             String expiryDate,
                             String issuePlace,
                             String issueingAuthority,
                             String profession,
                             String nationality,
                             String gender) {

        this.passportNumber = passportNumber;
        this.firstName = firstName;
        this.fatherName = fatherName;
        this.lastName = lastName;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.issueDate = issueDate;
        this.expiryDate = expiryDate;
        this.issuePlace = issuePlace;
        this.issueingAuthority = issueingAuthority;
        this.profession = profession;
        this.nationality = nationality;
        this.gender = gender;
    }

}
