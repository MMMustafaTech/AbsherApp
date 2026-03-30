package com.absher.absherapp.service;

import com.absher.absherapp.entity.BirthCertificate;
import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.repository.BirthCertificateRepository;
import org.springframework.stereotype.Service;

@Service
public class BirthCertificateService {

    private final BirthCertificateRepository repository;

    public BirthCertificateService(BirthCertificateRepository repository) {
        this.repository = repository;
    }

    public BirthCertificate getByNationalId(String nationalId) {
        return repository.findByNationalId(nationalId)
                .orElseThrow(() -> new NotFoundException("Birth certificate not found"));
    }
}