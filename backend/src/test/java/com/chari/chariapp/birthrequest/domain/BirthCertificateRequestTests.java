package com.chari.chariapp.birthrequest.domain;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.citizen.domain.CitizenId;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BirthCertificateRequestTests {

    private static final Instant NOW = Instant.parse("2026-08-29T00:00:00Z");

    @Test
    void preservesNewbornRegistrationTypeThroughReviewAndApproval() {
        AccountId employee = AccountId.newId();
        BirthCertificateRequest request = BirthCertificateRequest.submitted(
                CitizenId.newId(), BirthCertificateRequestKind.NEWBORN_REGISTRATION, null, NOW);

        BirthCertificateRequest approved = request.startReview(employee, NOW.plusSeconds(1))
                .decide(employee, true, null, NOW.plusSeconds(2));

        assertThat(approved.kind()).isEqualTo(BirthCertificateRequestKind.NEWBORN_REGISTRATION);
        assertThat(approved.status()).isEqualTo(BirthCertificateRequestStatus.APPROVED);
    }

    @Test
    void requiresReasonForBirthCertificateDataCorrection() {
        assertThatThrownBy(() -> BirthCertificateRequest.submitted(
                CitizenId.newId(), BirthCertificateRequestKind.DATA_CORRECTION, null, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void acceptsCompleteNewbornRegistrationDetails() {
        NewbornRegistrationDetails details = new NewbornRegistrationDetails(
                "Amani", "Saleh", LocalDate.parse("2026-08-01"), "N'Djamena", NewbornGender.FEMALE,
                "Saleh Ahmed", "123456789", "Mariam Ali", "987654321"
        );

        assertThat(details.childFirstName()).isEqualTo("Amani");
        assertThat(details.gender()).isEqualTo(NewbornGender.FEMALE);
    }

    @Test
    void rejectsMalformedParentNationalId() {
        assertThatThrownBy(() -> new NewbornRegistrationDetails(
                "Amani", "Saleh", LocalDate.parse("2026-08-01"), "N'Djamena", NewbornGender.FEMALE,
                "Saleh Ahmed", "father-id", "Mariam Ali", "987654321"
        )).isInstanceOf(IllegalArgumentException.class);
    }
}
