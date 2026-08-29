package com.chari.chariapp.request.domain;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.citizen.domain.CitizenId;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PassportRequestTests {
    private static final Instant NOW = Instant.parse("2026-08-28T00:00:00Z");

    @Test
    void followsTheExpectedReviewAndApprovalFlow() {
        AccountId employee = AccountId.newId();
        PassportRequest submitted = PassportRequest.submitted(CitizenId.newId(), NOW);

        PassportRequest approved = submitted.startReview(employee, NOW.plusSeconds(10))
                .decide(employee, true, null, NOW.plusSeconds(20));

        assertThat(approved.status()).isEqualTo(PassportRequestStatus.APPROVED);
        assertThat(approved.reviewedBy()).isEqualTo(employee);
    }

    @Test
    void rejectsInvalidTransitionsAndRejectionsWithoutAReason() {
        PassportRequest submitted = PassportRequest.submitted(CitizenId.newId(), NOW);
        AccountId reviewer = AccountId.newId();

        assertThatThrownBy(() -> submitted.decide(reviewer, true, null, NOW))
                .isInstanceOf(PassportRequestTransitionException.class);
        assertThatThrownBy(() -> submitted.startReview(reviewer, NOW).decide(reviewer, false, " ", NOW))
                .isInstanceOf(PassportRequestTransitionException.class);
        assertThatThrownBy(() -> submitted.startReview(reviewer, NOW).decide(AccountId.newId(), true, null, NOW))
                .isInstanceOf(PassportRequestTransitionException.class);
    }

    @Test
    void capturesThePassportServiceKindAndRequiresReasonsForLossDamageAndCorrections() {
        CitizenId citizenId = CitizenId.newId();

        PassportRequest renewal = PassportRequest.submitted(citizenId, PassportRequestKind.RENEWAL, null, NOW);
        assertThat(renewal.kind()).isEqualTo(PassportRequestKind.RENEWAL);

        assertThatThrownBy(() -> PassportRequest.submitted(citizenId, PassportRequestKind.LOST, " ", NOW))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("This passport request type requires a reason");
    }
}
