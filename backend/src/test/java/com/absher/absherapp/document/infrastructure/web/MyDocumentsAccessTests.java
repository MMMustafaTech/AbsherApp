package com.absher.absherapp.document.infrastructure.web;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.account.domain.EmailReference;
import com.absher.absherapp.account.infrastructure.security.JwtAccessTokenIssuer;
import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.citizen.domain.Citizen;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.citizen.domain.NationalIdReference;
import com.absher.absherapp.citizen.domain.PhoneReference;
import com.absher.absherapp.entity.Passport;
import com.absher.absherapp.repository.PassportRepository;
import com.absher.absherapp.document.infrastructure.migration.DocumentBackfillService;
import com.absher.absherapp.document.infrastructure.persistence.SpringDataPassportDocumentRepository;
import com.absher.absherapp.shared.security.PersonalDataProtector;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MyDocumentsAccessTests {

    @Autowired private MockMvc mockMvc;
    @Autowired private AccountStore accountStore;
    @Autowired private CitizenStore citizenStore;
    @Autowired private PassportRepository passportRepository;
    @Autowired private PersonalDataProtector dataProtector;
    @Autowired private JwtAccessTokenIssuer jwtAccessTokenIssuer;
    @Autowired private DocumentBackfillService documentBackfillService;
    @Autowired private SpringDataPassportDocumentRepository encryptedPassportDocuments;

    @Test
    void returnsOnlyTheAuthenticatedCitizensPassportAndBlocksTheLegacyRoute() throws Exception {
        String nationalId = "NAT-" + UUID.randomUUID();
        CitizenId citizenId = CitizenId.newId();
        citizenStore.save(new Citizen(
                citizenId,
                new NationalIdReference(dataProtector.lookup(nationalId), dataProtector.encrypt(nationalId)),
                new PhoneReference(dataProtector.lookup("phone-" + nationalId), dataProtector.encrypt("phone-" + nationalId)),
                Instant.now(), Instant.now()
        ));
        Account account = accountStore.save(new Account(
                AccountId.newId(), citizenId,
                new EmailReference(dataProtector.lookup("email-" + nationalId), dataProtector.encrypt("email-" + nationalId)),
                "password-hash", AccountStatus.ACTIVE, Set.of(AccountRole.CITIZEN), Instant.now()
        ));
        Passport passport = new Passport();
        passport.setNationalIdNumber(nationalId);
        passport.setPassportNumber("P-" + UUID.randomUUID());
        passport.setName("Test");
        passport.setLastName("Citizen");
        passport.setDateOfBirth(LocalDate.of(2000, 1, 1));
        passport.setPlaceOfBirth("N'Djamena");
        passport.setDateOfIssue(LocalDate.of(2025, 1, 1));
        passport.setDateOfExpiry(LocalDate.of(2030, 1, 1));
        passport.setPlaceOfIssue("N'Djamena");
        passport.setIssuingAuthority("Authority");
        passportRepository.save(passport);
        var firstBackfill = documentBackfillService.backfillCitizen(citizenStore.findById(citizenId).orElseThrow());
        var secondBackfill = documentBackfillService.backfillCitizen(citizenStore.findById(citizenId).orElseThrow());
        assertThat(firstBackfill.documentsCreated()).isEqualTo(1);
        assertThat(secondBackfill.documentsCreated()).isZero();
        assertThat(encryptedPassportDocuments.findFirstByCitizenIdOrderByRevisionDesc(citizenId.value().toString()).orElseThrow().encryptedPayload())
                .doesNotContain(passport.getPassportNumber());
        String accessToken = jwtAccessTokenIssuer.issue(account, Instant.now()).value();

        mockMvc.perform(get("/api/v1/me/documents/passport").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString(passport.getPassportNumber())));

        mockMvc.perform(get("/passport/" + nationalId).header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());

        accountStore.updateStatus(account.id(), AccountStatus.DISABLED);
        mockMvc.perform(get("/api/v1/me/documents/passport").header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isUnauthorized());
    }
}
