package com.absher.absherapp.controller;


import com.absher.absherapp.dto.PassportResponse;
import com.absher.absherapp.entity.Passport;
import com.absher.absherapp.service.PassportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/passport")
public class PassportController {
    private final PassportService passportService;

    public PassportController(PassportService passportService) {
        this.passportService = passportService;
    }

    @GetMapping("/{nationalId}")
    public ResponseEntity<?> getPassport(@PathVariable String nationalId) {

        Passport passport = passportService.getPassportByNationalId(nationalId);

        PassportResponse response = new PassportResponse(
                passport.getPassportNumber(),
                passport.getName(),
                passport.getFatherName(),
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
