package com.absher.absherapp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    private String nationalId;
    private String password;
    private String email;

}