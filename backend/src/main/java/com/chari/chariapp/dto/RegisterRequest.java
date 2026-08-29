package com.chari.chariapp.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class RegisterRequest {

    private String nationalId;
    private String password;
    private String email;

}