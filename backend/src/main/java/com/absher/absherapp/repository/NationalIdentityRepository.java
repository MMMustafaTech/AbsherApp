package com.absher.absherapp.repository;

import com.absher.absherapp.entity.NationalIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NationalIdentityRepository extends JpaRepository<NationalIdentity, Long> {

    boolean existsByNationalIdNumber(String nationalIdNumber);

}