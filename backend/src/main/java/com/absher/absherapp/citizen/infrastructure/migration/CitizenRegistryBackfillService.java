package com.absher.absherapp.citizen.infrastructure.migration;

import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.citizen.domain.Citizen;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.citizen.domain.NationalIdReference;
import com.absher.absherapp.entity.NationalIdentity;
import com.absher.absherapp.repository.NationalIdRepository;
import com.absher.absherapp.shared.security.PersonalDataNormalizer;
import com.absher.absherapp.shared.security.PersonalDataProtector;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;

/**
 * Explicit, idempotent migration from legacy national identities to the protected citizen registry.
 * Legacy tables do not contain phone numbers, so this migration deliberately creates no verified phone.
 */
@Service
public class CitizenRegistryBackfillService {

    private final NationalIdRepository legacyNationalIdentities;
    private final CitizenStore citizenStore;
    private final PersonalDataProtector dataProtector;
    private final Clock clock;

    public CitizenRegistryBackfillService(
            NationalIdRepository legacyNationalIdentities,
            CitizenStore citizenStore,
            PersonalDataProtector dataProtector,
            Clock clock
    ) {
        this.legacyNationalIdentities = legacyNationalIdentities;
        this.citizenStore = citizenStore;
        this.dataProtector = dataProtector;
        this.clock = clock;
    }

    @Transactional
    public BackfillReport backfillAll() {
        int sourceRecords = 0;
        int created = 0;
        int existing = 0;
        int skipped = 0;

        for (NationalIdentity legacyIdentity : legacyNationalIdentities.findAll()) {
            sourceRecords++;
            String legacyNationalId = legacyIdentity.getNationalIdNumber();
            if (legacyNationalId == null || legacyNationalId.isBlank()) {
                skipped++;
                continue;
            }

            String lookup = dataProtector.lookup(PersonalDataNormalizer.nationalId(legacyNationalId));
            if (citizenStore.existsByNationalIdLookup(lookup)) {
                existing++;
                continue;
            }

            citizenStore.save(new Citizen(
                    CitizenId.newId(),
                    new NationalIdReference(lookup, dataProtector.encrypt(legacyNationalId)),
                    null,
                    null,
                    Instant.now(clock)
            ));
            created++;
        }
        return new BackfillReport(sourceRecords, created, existing, skipped);
    }

    public record BackfillReport(int sourceRecords, int citizensCreated, int citizensAlreadyPresent, int recordsSkipped) {
    }
}
