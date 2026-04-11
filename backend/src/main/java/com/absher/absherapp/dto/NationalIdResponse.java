package com.absher.absherapp.dto;

import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class NationalIdResponse {

    private String nationalIdNumber;
    private String firstName;
    private String lastName;
    private String dataofBirth;
    private String nationality;

}
