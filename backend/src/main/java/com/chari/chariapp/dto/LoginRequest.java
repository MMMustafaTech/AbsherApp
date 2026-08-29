package com.chari.chariapp.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
    private String nationalId;
    private String password;
}
