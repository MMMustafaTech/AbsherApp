package com.chari.chariapp.service;

import com.chari.chariapp.entity.BirthCertificate;
import com.chari.chariapp.exception.NotFoundException;
import com.chari.chariapp.repository.BirthCertificateRepository;
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