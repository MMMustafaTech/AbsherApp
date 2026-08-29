package com.chari.chariapp.account.application;

import com.chari.chariapp.account.application.port.out.AccountStore;
import com.chari.chariapp.account.application.port.out.EnrollmentChallengeStore;
import com.chari.chariapp.account.application.port.out.PasswordHasher;
import com.chari.chariapp.account.domain.Account;
import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.account.domain.EnrollmentChallenge;
import com.chari.chariapp.account.domain.EnrollmentChallengeId;
import com.chari.chariapp.account.domain.VerificationChallengePurpose;
import com.chari.chariapp.citizen.domain.CitizenId;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CreateVerifiedCitizenAccountServiceTests {

    private static final Instant NOW = Instant.parse("2026-08-28T00:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);
    private static final String EMAIL_LOOKUP = "c".repeat(64);

    @Test
    void createsAnActiveCitizenAccountAndConsumesTheVerifiedProof() {
        InMemoryChallengeStore challengeStore = new InMemoryChallengeStore(verifiedChallenge());
        CapturingAccountStore accountStore = new CapturingAccountStore();
        CreateVerifiedCitizenAccountService service = service(accountStore, challengeStore);

        AccountId accountId = service.create(new CreateVerifiedCitizenAccountCommand(
                challengeStore.challenge.id(), EMAIL_LOOKUP, "encrypted-email", "a-secure-password"
        ));

        assertThat(accountId).isEqualTo(accountStore.saved.id());
        assertThat(accountStore.saved.status()).isEqualTo(com.chari.chariapp.account.domain.AccountStatus.ACTIVE);
        assertThat(accountStore.saved.citizenId()).isEqualTo(challengeStore.challenge.citizenId());
        assertThat(accountStore.saved.roles()).containsExactly(com.chari.chariapp.account.domain.AccountRole.CITIZEN);
        assertThat(challengeStore.challenge.enrollmentConsumedAt()).isEqualTo(NOW);
    }

    @Test
    void rejectsAnUnverifiedProofWithoutCreatingAnAccount() {
        EnrollmentChallenge unverified = new EnrollmentChallenge(
                EnrollmentChallengeId.newId(), CitizenId.newId(), VerificationChallengePurpose.ACCOUNT_ENROLLMENT,
                "b".repeat(64), "encrypted-phone", "hash",
                NOW.plusSeconds(300), 0, null, null
        );
        InMemoryChallengeStore challengeStore = new InMemoryChallengeStore(unverified);
        CapturingAccountStore accountStore = new CapturingAccountStore();

        assertThatThrownBy(() -> service(accountStore, challengeStore).create(new CreateVerifiedCitizenAccountCommand(
                unverified.id(), EMAIL_LOOKUP, "encrypted-email", "a-secure-password"
        ))).isInstanceOf(EnrollmentProofUnavailableException.class);

        assertThat(accountStore.saved).isNull();
        assertThat(challengeStore.challenge.enrollmentConsumedAt()).isNull();
    }

    @Test
    void rejectsAPhoneVerificationProofForAccountCreation() {
        EnrollmentChallenge phoneProof = new EnrollmentChallenge(
                EnrollmentChallengeId.newId(), CitizenId.newId(), VerificationChallengePurpose.CITIZEN_PHONE_VERIFICATION,
                "b".repeat(64), "encrypted-phone", "hash", NOW.plusSeconds(300), 0, NOW.minusSeconds(1), null
        );
        CapturingAccountStore accountStore = new CapturingAccountStore();

        assertThatThrownBy(() -> service(accountStore, new InMemoryChallengeStore(phoneProof)).create(
                new CreateVerifiedCitizenAccountCommand(phoneProof.id(), EMAIL_LOOKUP, "encrypted-email", "a-secure-password")
        )).isInstanceOf(EnrollmentProofUnavailableException.class);

        assertThat(accountStore.saved).isNull();
    }

    private static CreateVerifiedCitizenAccountService service(
            CapturingAccountStore accountStore, InMemoryChallengeStore challengeStore
    ) {
        PasswordHasher passwordHasher = new PasswordHasher() {
            @Override
            public String hash(String rawPassword) {
                return "hash:" + rawPassword;
            }

            @Override
            public boolean matches(String rawPassword, String passwordHash) {
                return hash(rawPassword).equals(passwordHash);
            }
        };
        return new CreateVerifiedCitizenAccountService(accountStore, challengeStore, passwordHasher, CLOCK);
    }

    private static EnrollmentChallenge verifiedChallenge() {
        return new EnrollmentChallenge(
                EnrollmentChallengeId.newId(), CitizenId.newId(), VerificationChallengePurpose.ACCOUNT_ENROLLMENT,
                "b".repeat(64), "encrypted-phone", "hash",
                NOW.plusSeconds(300), 0, NOW.minusSeconds(1), null
        );
    }

    private static final class InMemoryChallengeStore implements EnrollmentChallengeStore {
        private EnrollmentChallenge challenge;

        private InMemoryChallengeStore(EnrollmentChallenge challenge) {
            this.challenge = challenge;
        }

        @Override
        public EnrollmentChallenge save(EnrollmentChallenge challenge) {
            this.challenge = challenge;
            return challenge;
        }

        @Override
        public Optional<EnrollmentChallenge> findById(EnrollmentChallengeId id) {
            return challenge.id().equals(id) ? Optional.of(challenge) : Optional.empty();
        }

        @Override
        public boolean consumeVerified(EnrollmentChallengeId id, Instant consumedAt) {
            if (!challenge.id().equals(id) || !challenge.isVerified() || challenge.isExpired(consumedAt)
                    || challenge.isEnrollmentConsumed()) {
                return false;
            }
            challenge = challenge.markEnrollmentConsumed(consumedAt);
            return true;
        }
    }

    private static final class CapturingAccountStore implements AccountStore {
        private Account saved;

        @Override
        public boolean existsByEmailLookup(String emailLookup) {
            return false;
        }

        @Override
        public boolean existsByCitizenId(CitizenId citizenId) {
            return false;
        }

        @Override
        public Optional<Account> findByEmailLookup(String emailLookup) {
            return Optional.empty();
        }

        @Override
        public Optional<Account> findById(AccountId accountId) {
            return saved != null && saved.id().equals(accountId) ? Optional.of(saved) : Optional.empty();
        }

        @Override
        public Account save(Account account) {
            saved = account;
            return account;
        }
    }
}
