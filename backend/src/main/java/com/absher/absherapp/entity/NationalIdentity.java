package com.absher.absherapp.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "national_identities")
public class NationalIdentity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "national_id_number", unique = true, nullable = false)
    private String nationalIdNumber;

    @Column
    private String name;

    @Column(name = "father_name")
    private String fatherName;

    @Column(name = "mother_name")
    private String motherName;

    @Column(name = "last_name")
    private String lastName;

    private String nationality;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "place_of_issue")
    private String placeOfIssue;

    @Column(name = "date_of_issue")
    private LocalDate dateOfIssue;

    @Column(name = "date_of_expiry")
    private LocalDate dateOfExpiry;
}