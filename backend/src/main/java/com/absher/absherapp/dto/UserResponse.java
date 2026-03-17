package com.absher.absherapp.dto;

import lombok.Getter;

@Getter
public class UserResponse {
    private final Long id;
    private final String nationalId;
    private final String email;

    public UserResponse(Long id, String nationalId, String email) {
        this.id = id;
        this.nationalId = nationalId;
        this.email = email;
    }

}
