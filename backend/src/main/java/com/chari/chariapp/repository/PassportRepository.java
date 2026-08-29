package com.chari.chariapp.repository;

import com.chari.chariapp.entity.Passport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PassportRepository extends JpaRepository<Passport, Long> {
    Optional<Passport> findByNationalIdNumber(String nationalIdNumber);

}