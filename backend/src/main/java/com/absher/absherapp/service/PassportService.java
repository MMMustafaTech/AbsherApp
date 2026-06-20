package com.absher.absherapp.service;

import com.absher.absherapp.entity.Passport;
import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.repository.PassportRepository;
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