package com.chari.chariapp.service;

import com.chari.chariapp.entity.Passport;
import com.chari.chariapp.exception.NotFoundException;
import com.chari.chariapp.repository.PassportRepository;
import org.springframework.stereotype.Service;

@Service
public class PassportService {

    private final PassportRepository passportRepository;

    public PassportService(PassportRepository PassportRepository) {
        this.passportRepository = PassportRepository;
    }

    public Passport getPassportByNationalId(String nationalId) {
        return passportRepository.findByNationalIdNumber(nationalId)
                .orElseThrow(() -> new NotFoundException("Passport not found"));
    }
}