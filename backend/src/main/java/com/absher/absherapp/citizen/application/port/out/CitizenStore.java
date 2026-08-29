package com.absher.absherapp.citizen.application.port.out;

import com.absher.absherapp.citizen.domain.Citizen;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.citizen.domain.PhoneReference;

import java.time.Instant;
import java.util.Optional;

public interface CitizenStore {

    boolean existsByNationalIdLookup(String nationalIdLookup);

    Optional<Citizen> findByNationalIdLookup(String nationalIdLookup);

    Optional<Citizen> findById(CitizenId citizenId);

    Citizen updateVerifiedPhone(CitizenId citizenId, PhoneReference verifiedPhone, Instant verifiedAt);

    Citizen save(Citizen citizen);
}
