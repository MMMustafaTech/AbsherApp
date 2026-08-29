package com.absher.absherapp.citizen.infrastructure.persistence;

import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.citizen.domain.Citizen;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.citizen.domain.PhoneReference;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.NoSuchElementException;
import java.util.Optional;

@Repository
public class JpaCitizenStore implements CitizenStore {

    private final SpringDataCitizenRepository repository;

    public JpaCitizenStore(SpringDataCitizenRepository repository) {
        this.repository = repository;
    }

    @Override
    public boolean existsByNationalIdLookup(String nationalIdLookup) {
        return repository.existsByNationalIdLookup(nationalIdLookup);
    }

    @Override
    public Optional<Citizen> findByNationalIdLookup(String nationalIdLookup) {
        return repository.findByNationalIdLookup(nationalIdLookup).map(CitizenJpaEntity::toDomain);
    }

    @Override
    public Optional<Citizen> findById(com.absher.absherapp.citizen.domain.CitizenId citizenId) {
        return repository.findById(citizenId.value().toString()).map(CitizenJpaEntity::toDomain);
    }

    @Override
    public Citizen updateVerifiedPhone(CitizenId citizenId, PhoneReference verifiedPhone, Instant verifiedAt) {
        CitizenJpaEntity entity = repository.findById(citizenId.value().toString())
                .orElseThrow(() -> new NoSuchElementException("Citizen not found"));
        entity.replaceVerifiedPhone(verifiedPhone, verifiedAt);
        return repository.save(entity).toDomain();
    }

    @Override
    public Citizen save(Citizen citizen) {
        return repository.save(CitizenJpaEntity.fromDomain(citizen)).toDomain();
    }
}
