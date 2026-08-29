package com.absher.absherapp.identityrequest.domain;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.citizen.domain.CitizenId;
import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class NationalIdentityRequestTests {
    private static final Instant NOW = Instant.parse("2026-08-29T00:00:00Z");
    @Test
    void preservesTheRequestTypeThroughReviewAndApproval() {
        AccountId employee = AccountId.newId();
        NationalIdentityRequest request = NationalIdentityRequest.submitted(CitizenId.newId(), NationalIdentityRequestKind.RENEWAL, null, NOW);
        NationalIdentityRequest approved = request.startReview(employee, NOW.plusSeconds(1)).decide(employee, true, null, NOW.plusSeconds(2));
        assertThat(approved.kind()).isEqualTo(NationalIdentityRequestKind.RENEWAL);
        assertThat(approved.status()).isEqualTo(NationalIdentityRequestStatus.APPROVED);
    }
    @Test
    void requiresReasonForLostIdentityRequest() {
        assertThatThrownBy(() -> NationalIdentityRequest.submitted(CitizenId.newId(), NationalIdentityRequestKind.LOST, null, NOW))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
