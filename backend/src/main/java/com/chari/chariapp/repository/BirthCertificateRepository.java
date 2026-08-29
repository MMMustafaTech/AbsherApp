package com.chari.chariapp.repository;

import com.chari.chariapp.entity.BirthCertificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BirthCertificateRepository extends JpaRepository<BirthCertificate, Long> {

    Optional<BirthCertificate> findByNationalId(String nationalId);
}