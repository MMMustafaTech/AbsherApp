package com.absher.absherapp.controller;


import com.absher.absherapp.dto.NationalIdResponse;
import com.absher.absherapp.service.NationalIdService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/national-ids")

public class NationalIdController {
    private final NationalIdService nationalIdService;

    public NationalIdController(NationalIdService nationalIdService) {
        this.nationalIdService = nationalIdService;
    }

    @GetMapping("/{nationalIdNumber}")
    public ResponseEntity<NationalIdResponse> getByIdNumber(@PathVariable String nationalIdNumber) {
        NationalIdResponse response = nationalIdService.getByIdNumber(nationalIdNumber);

        return ResponseEntity.ok(response);
    }

}
