package com.chari.chariapp.citizen.application;

import com.chari.chariapp.citizen.application.port.out.CitizenStore;
import com.chari.chariapp.citizen.domain.Citizen;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateCitizenServiceTests {

    private static final String LOOKUP = "a".repeat(64);

    @Test
    void createsCitizenWithTheSuppliedProtectedIdentityReference() {
        InMemoryCitizenStore store = new InMemoryCitizenStore();
        CreateCitizenService service = new CreateCitizenService(
                store,
                Clock.fixed(Instant.parse("2026-08-28T00:00:00Z"), ZoneOffset.UTC)
        );

        var citizenId = service.create(new CreateCitizenCommand(
                LOOKUP,
                "encrypted-value",
                "c".repeat(64),
                "encrypted-phone",
                Instant.parse("2026-08-27T00:00:00Z")
        ));

        assertThat(citizenId).isNotNull();
        assertThat(store.citizensByLookup).containsKey(LOOKUP);
        assertThat(store.citizensByLookup.get(LOOKUP).createdAt())
                .isEqualTo(Instant.parse("2026-08-28T00:00:00Z"));
    }

    @Test
    void rejectsDuplicateNationalIdLookups() {
        InMemoryCitizenStore store = new InMemoryCitizenStore();
        CreateCitizenService service = new CreateCitizenService(store, Clock.systemUTC());
        service.create(new CreateCitizenCommand(
                LOOKUP, "encrypted-value", "c".repeat(64), "encrypted-phone", Instant.now()
        ));

        assertThatThrownBy(() -> service.create(new CreateCitizenCommand(
                LOOKUP, "another-value", "d".repeat(64), "another-phone", Instant.now()
        )))
                .isInstanceOf(CitizenAlreadyExistsException.class);
    }

    private static final class InMemoryCitizenStore implements CitizenStore {
        private final Map<String, Citizen> citizensByLookup = new HashMap<>();

        @Override
        public boolean existsByNationalIdLookup(String nationalIdLookup) {
            return citizensByLookup.containsKey(nationalIdLookup);
        }

        @Override
        public java.util.Optional<Citizen> findByNationalIdLookup(String nationalIdLookup) {
            return java.util.Optional.ofNullable(citizensByLookup.get(nationalIdLookup));
        }

        @Override
        public java.util.Optional<Citizen> findById(com.chari.chariapp.citizen.domain.CitizenId citizenId) {
            return citizensByLookup.values().stream().filter(citizen -> citizen.id().equals(citizenId)).findFirst();
        }

        @Override
        public Citizen updateVerifiedPhone(
                com.chari.chariapp.citizen.domain.CitizenId citizenId,
                com.chari.chariapp.citizen.domain.PhoneReference verifiedPhone,
                Instant verifiedAt
        ) {
            Citizen existing = findById(citizenId).orElseThrow();
            Citizen updated = new Citizen(existing.id(), existing.nationalId(), verifiedPhone, verifiedAt, existing.createdAt());
            citizensByLookup.put(updated.nationalId().lookup(), updated);
            return updated;
        }

        @Override
        public Citizen save(Citizen citizen) {
            citizensByLookup.put(citizen.nationalId().lookup(), citizen);
            return citizen;
        }
    }
}
