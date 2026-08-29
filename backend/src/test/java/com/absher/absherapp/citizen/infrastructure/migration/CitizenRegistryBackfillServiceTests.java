package com.absher.absherapp.citizen.infrastructure.migration;

import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.entity.NationalIdentity;
import com.absher.absherapp.repository.NationalIdRepository;
import com.absher.absherapp.shared.security.PersonalDataNormalizer;
import com.absher.absherapp.shared.security.PersonalDataProtector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class CitizenRegistryBackfillServiceTests {

    @Autowired private NationalIdRepository legacyNationalIdentities;
    @Autowired private CitizenStore citizenStore;
    @Autowired private CitizenRegistryBackfillService backfillService;
    @Autowired private PersonalDataProtector dataProtector;

    @Test
    void createsAnEncryptedRegistryCitizenWithoutInventingAPhoneAndIsIdempotent() {
        String legacyNationalId = "NAT-" + UUID.randomUUID();
        NationalIdentity identity = new NationalIdentity();
        identity.setNationalIdNumber(legacyNationalId);
        legacyNationalIdentities.save(identity);

        CitizenRegistryBackfillService.BackfillReport first = backfillService.backfillAll();
        String lookup = dataProtector.lookup(PersonalDataNormalizer.nationalId(legacyNationalId));
        var migratedCitizen = citizenStore.findByNationalIdLookup(lookup).orElseThrow();

        assertThat(first.citizensCreated()).isPositive();
        assertThat(migratedCitizen.nationalId().ciphertext()).doesNotContain(legacyNationalId);
        assertThat(migratedCitizen.verifiedPhoneOptional()).isEmpty();

        CitizenRegistryBackfillService.BackfillReport second = backfillService.backfillAll();
        assertThat(second.citizensCreated()).isZero();
        assertThat(second.citizensAlreadyPresent()).isPositive();
    }
}
