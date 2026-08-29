package com.chari.chariapp.controller;


import com.chari.chariapp.dto.PassportResponse;
import com.chari.chariapp.entity.Passport;
import com.chari.chariapp.service.PassportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
