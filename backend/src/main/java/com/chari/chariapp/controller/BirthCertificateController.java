package com.chari.chariapp.controller;

import com.chari.chariapp.dto.BirthCertificateResponse;
import com.chari.chariapp.entity.BirthCertificate;
import com.chari.chariapp.service.BirthCertificateService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/birth-certificate")
public class BirthCertificateController {

    private final BirthCertificateService service;

    public BirthCertificateController(BirthCertificateService service) {
        this.service = service;
    }

    @GetMapping("/{nationalId}")
    public ResponseEntity<?> get(@PathVariable String nationalId) {

        BirthCertificate bc = service.getByNationalId(nationalId);

        BirthCertificateResponse response = new BirthCertificateResponse(
                bc.getCertificateNumber(),
                bc.getFullName(),
                bc.getGender(),
                bc.getBirthDate().toString(),
                bc.getBirthPlace(),

                bc.getFatherName(),
                bc.getFatherBirthDate().toString(),
                bc.getFatherBirthPlace(),
                bc.getFatherProfession(),

                bc.getMotherName(),
                bc.getMotherBirthDate().toString(),
                bc.getMotherBirthPlace(),
                bc.getMotherProfession(),

                bc.getDeclarationDate().toString(),
                bc.getAddress()
        );

        return ResponseEntity.ok(response);
    }
}