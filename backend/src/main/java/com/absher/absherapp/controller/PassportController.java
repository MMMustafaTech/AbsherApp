package com.absher.absherapp.controller;


import com.absher.absherapp.dto.PassportRequest;
import com.absher.absherapp.dto.PassportResponse;
import com.absher.absherapp.entity.Passport;
import com.absher.absherapp.service.PassportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/passport")
public class PassportController {
    private final PassportService passportService;

    public PassportController(PassportService passportService) {
        this.passportService = passportService;
    }

    @PostMapping
    public ResponseEntity<?> getPassport(@RequestBody PassportRequest request) {

        Passport passport = passportService.getPassportByNationalId(request.getNationalId());

        PassportResponse response = new PassportResponse(
                passport.getPassportNumber(),
                passport.getName(),
                passport.getLastName(),
                passport.getDateOfBirth().toString(),
                passport.getPlaceOfBirth(),
                passport.getDateOfIssue().toString(),
                passport.getDateOfExpiry().toString(),
                passport.getPlaceOfIssue(),
                passport.getIssuingAuthority(),
                passport.getJob(),
                passport.getNationality(),
                passport.getSex()
        );

        return ResponseEntity.ok(response);
    }
}
