package com.absher.absherapp.citizen.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataCitizenRepository extends JpaRepository<CitizenJpaEntity, String> {

    boolean existsByNationalIdLookup(String nationalIdLookup);

    Optional<CitizenJpaEntity> findByNationalIdLookup(String nationalIdLookup);
}
