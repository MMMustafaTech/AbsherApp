package com.chari.chariapp.repository;

import com.chari.chariapp.entity.NationalIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NationalIdRepository extends JpaRepository<NationalIdentity, Long> {

    boolean existsByNationalIdNumber(String nationalIdNumber);

    Optional<NationalIdentity> findByNationalIdNumber(String nationalIdNumber);

}