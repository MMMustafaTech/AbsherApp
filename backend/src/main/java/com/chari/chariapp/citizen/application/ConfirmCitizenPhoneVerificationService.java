package com.chari.chariapp.citizen.application;

import com.chari.chariapp.account.application.InvalidEnrollmentOtpException;
import com.chari.chariapp.account.application.VerifyEnrollmentOtpCommand;
import com.chari.chariapp.account.application.VerifyEnrollmentOtpUseCase;
import com.chari.chariapp.account.application.port.out.AccountStore;
import com.chari.chariapp.account.application.port.out.EnrollmentChallengeStore;
import com.chari.chariapp.account.domain.Account;
import com.chari.chariapp.account.domain.AccountRole;
import com.chari.chariapp.account.domain.AccountStatus;
import com.chari.chariapp.account.domain.EnrollmentChallenge;
import com.chari.chariapp.account.domain.VerificationChallengePurpose;
import com.chari.chariapp.citizen.application.port.out.CitizenStore;
import com.chari.chariapp.citizen.domain.PhoneReference;
import com.chari.chariapp.shared.application.port.out.OperationalAuditStore;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/** Validates the one-time proof and then atomically assigns the encrypted phone to that citizen. */
public class ConfirmCitizenPhoneVerificationService implements ConfirmCitizenPhoneVerificationUseCase {
    private final CitizenStore citizenStore;
    private final AccountStore accountStore;
    private final EnrollmentChallengeStore challengeStore;
    private final VerifyEnrollmentOtpUseCase verifyOtp;
    private final OperationalAuditStore auditStore;
    private final Clock clock;

    public ConfirmCitizenPhoneVerificationService(
            CitizenStore citizenStore,
            AccountStore accountStore,
            EnrollmentChallengeStore challengeStore,
            VerifyEnrollmentOtpUseCase verifyOtp,
            OperationalAuditStore auditStore,
            Clock clock
    ) {
        this.citizenStore = Objects.requireNonNull(citizenStore, "Citizen store is required");
        this.accountStore = Objects.requireNonNull(accountStore, "Account store is required");
        this.challengeStore = Objects.requireNonNull(challengeStore, "Challenge store is required");
        this.verifyOtp = Objects.requireNonNull(verifyOtp, "OTP verifier is required");
        this.auditStore = Objects.requireNonNull(auditStore, "Audit store is required");
        this.clock = Objects.requireNonNull(clock, "Clock is required");
    }

    @Override
    @Transactional
    public void confirm(ConfirmCitizenPhoneVerificationCommand command) {
        Objects.requireNonNull(command, "Phone verification confirmation is required");
        requireAuthorizedOperator(command.operatorId());
        EnrollmentChallenge challenge = challengeStore.findById(command.challengeId())
                .orElseThrow(InvalidEnrollmentOtpException::new);
        if (challenge.purpose() != VerificationChallengePurpose.CITIZEN_PHONE_VERIFICATION
                || !challenge.citizenId().equals(command.citizenId())) {
            throw new InvalidEnrollmentOtpException();
        }

        verifyOtp.verify(new VerifyEnrollmentOtpCommand(command.challengeId(), command.code()));
        challenge = challengeStore.findById(command.challengeId()).orElseThrow(InvalidEnrollmentOtpException::new);
        if (!challengeStore.consumeVerified(challenge.id(), Instant.now(clock))) {
            throw new InvalidEnrollmentOtpException();
        }
        String ciphertext = challenge.destinationCiphertextOptional().orElseThrow(InvalidEnrollmentOtpException::new);
        Instant now = Instant.now(clock);
        citizenStore.updateVerifiedPhone(challenge.citizenId(), new PhoneReference(challenge.destinationLookup(), ciphertext), now);
        auditStore.record(command.operatorId().value().toString(), "CITIZEN_PHONE_VERIFICATION_CONFIRMED", "CITIZEN",
                challenge.citizenId().value().toString(), "{\"challengeId\":\"" + challenge.id().value() + "\"}", now);
    }

    private void requireAuthorizedOperator(com.chari.chariapp.account.domain.AccountId operatorId) {
        Account operator = accountStore.findById(operatorId).orElseThrow(CitizenPhoneVerificationException::new);
        if (operator.status() != AccountStatus.ACTIVE
                || (!operator.roles().contains(AccountRole.EMPLOYEE) && !operator.roles().contains(AccountRole.ADMIN))) {
            throw new CitizenPhoneVerificationException();
        }
    }
}
