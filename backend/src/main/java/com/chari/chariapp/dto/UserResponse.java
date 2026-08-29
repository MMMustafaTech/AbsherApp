package com.chari.chariapp.dto;

import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;
    private final String nationalId;
    private final String email;
    private final String name;

    public UserResponse(Long id, String nationalId, String email, String name) {
        this.id = id;
        this.nationalId = nationalId;
        this.email = email;
        this.name = name;
    }
}
