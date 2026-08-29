package com.absher.absherapp.citizen.application;

import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.citizen.domain.Citizen;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.citizen.domain.NationalIdReference;
import com.absher.absherapp.citizen.domain.PhoneReference;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public class CreateCitizenService implements CreateCitizenUseCase {

    private final CitizenStore citizenStore;
    private final Clock clock;

    public CreateCitizenService(CitizenStore citizenStore, Clock clock) {
        this.citizenStore = Objects.requireNonNull(citizenStore, "Citizen store is required");
        this.clock = Objects.requireNonNull(clock, "Clock is required");
    }

    @Override
    public CitizenId create(CreateCitizenCommand command) {
        Objects.requireNonNull(command, "Create citizen command is required");
        NationalIdReference nationalId = new NationalIdReference(
                command.nationalIdLookup(),
                command.encryptedNationalId()
        );
        PhoneReference verifiedPhone = new PhoneReference(
                command.phoneLookup(),
                command.encryptedPhone()
        );

        if (citizenStore.existsByNationalIdLookup(nationalId.lookup())) {
            throw new CitizenAlreadyExistsException();
        }

        Citizen citizen = new Citizen(
                CitizenId.newId(),
                nationalId,
                verifiedPhone,
                command.phoneVerifiedAt(),
                Instant.now(clock)
        );
        return citizenStore.save(citizen).id();
    }
}
