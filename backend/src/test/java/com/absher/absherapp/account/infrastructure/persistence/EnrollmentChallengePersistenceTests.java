package com.absher.absherapp.account.infrastructure.persistence;

import com.absher.absherapp.account.application.port.out.EnrollmentChallengeStore;
import com.absher.absherapp.account.domain.EnrollmentChallenge;
import com.absher.absherapp.account.domain.EnrollmentChallengeId;
import com.absher.absherapp.account.domain.VerificationChallengePurpose;
import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.citizen.domain.Citizen;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.citizen.domain.NationalIdReference;
import com.absher.absherapp.citizen.domain.PhoneReference;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class EnrollmentChallengePersistenceTests {

    @Autowired
    private CitizenStore citizenStore;

    @Autowired
    private EnrollmentChallengeStore challengeStore;

    @Test
    void consumesAStoredVerifiedChallengeExactlyOnce() {
        Instant now = Instant.parse("2026-08-28T00:00:00Z");
        Citizen citizen = new Citizen(
                CitizenId.newId(),
                new NationalIdReference("a".repeat(64), "ciphertext-national-id"),
                new PhoneReference("b".repeat(64), "ciphertext-phone"),
                now.minusSeconds(60),
                now.minusSeconds(60)
        );
        citizenStore.save(citizen);
        EnrollmentChallenge challenge = new EnrollmentChallenge(
                EnrollmentChallengeId.newId(), citizen.id(), VerificationChallengePurpose.ACCOUNT_ENROLLMENT,
                "b".repeat(64), "encrypted-phone", "hash",
                now.plusSeconds(300), 0, now.minusSeconds(1), null
        );
        challengeStore.save(challenge);

        assertThat(challengeStore.consumeVerified(challenge.id(), now)).isTrue();
        assertThat(challengeStore.consumeVerified(challenge.id(), now.plusSeconds(1))).isFalse();
        assertThat(challengeStore.findById(challenge.id()).orElseThrow().enrollmentConsumedAt()).isEqualTo(now);
    }
}
