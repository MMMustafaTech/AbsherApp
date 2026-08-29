package com.absher.absherapp.citizen.application;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.application.port.out.EnrollmentChallengeStore;
import com.absher.absherapp.account.application.port.out.OtpCodeGenerator;
import com.absher.absherapp.account.application.port.out.OtpSender;
import com.absher.absherapp.account.application.port.out.VerificationCodeHasher;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.account.domain.EnrollmentChallenge;
import com.absher.absherapp.account.domain.EnrollmentChallengeId;
import com.absher.absherapp.account.domain.VerificationChallengePurpose;
import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/** Starts a staff-authorized OTP proof before changing a citizen's registered phone. */
public class RequestCitizenPhoneVerificationService implements RequestCitizenPhoneVerificationUseCase {
    private static final Duration OTP_LIFETIME = Duration.ofMinutes(5);

    private final CitizenStore citizenStore;
    private final AccountStore accountStore;
    private final EnrollmentChallengeStore challengeStore;
    private final OtpCodeGenerator codeGenerator;
    private final VerificationCodeHasher codeHasher;
    private final OtpSender otpSender;
    private final OperationalAuditStore auditStore;
    private final Clock clock;

    public RequestCitizenPhoneVerificationService(
            CitizenStore citizenStore,
            AccountStore accountStore,
            EnrollmentChallengeStore challengeStore,
            OtpCodeGenerator codeGenerator,
            VerificationCodeHasher codeHasher,
            OtpSender otpSender,
            OperationalAuditStore auditStore,
            Clock clock
    ) {
        this.citizenStore = Objects.requireNonNull(citizenStore, "Citizen store is required");
        this.accountStore = Objects.requireNonNull(accountStore, "Account store is required");
        this.challengeStore = Objects.requireNonNull(challengeStore, "Challenge store is required");
        this.codeGenerator = Objects.requireNonNull(codeGenerator, "OTP generator is required");
        this.codeHasher = Objects.requireNonNull(codeHasher, "Verification code hasher is required");
        this.otpSender = Objects.requireNonNull(otpSender, "OTP sender is required");
        this.auditStore = Objects.requireNonNull(auditStore, "Audit store is required");
        this.clock = Objects.requireNonNull(clock, "Clock is required");
    }

    @Override
    public EnrollmentChallengeId request(RequestCitizenPhoneVerificationCommand command) {
        Objects.requireNonNull(command, "Phone verification request is required");
        requireAuthorizedOperator(command.operatorId());
        citizenStore.findById(command.citizenId()).orElseThrow(CitizenPhoneVerificationException::new);

        Instant now = Instant.now(clock);
        String code = codeGenerator.generate();
        EnrollmentChallenge challenge = new EnrollmentChallenge(
                EnrollmentChallengeId.newId(),
                command.citizenId(),
                VerificationChallengePurpose.CITIZEN_PHONE_VERIFICATION,
                command.phone().lookup(),
                command.phone().ciphertext(),
                codeHasher.hash(code),
                now.plus(OTP_LIFETIME),
                0,
                null,
                null
        );
        challengeStore.save(challenge);
        otpSender.sendEnrollmentCode(command.phone(), code);
        auditStore.record(command.operatorId().value().toString(), "CITIZEN_PHONE_VERIFICATION_REQUESTED", "CITIZEN",
                command.citizenId().value().toString(), "{\"challengeId\":\"" + challenge.id().value() + "\"}", now);
        return challenge.id();
    }

    private void requireAuthorizedOperator(com.absher.absherapp.account.domain.AccountId operatorId) {
        Account operator = accountStore.findById(operatorId).orElseThrow(CitizenPhoneVerificationException::new);
        if (operator.status() != AccountStatus.ACTIVE
                || (!operator.roles().contains(AccountRole.EMPLOYEE) && !operator.roles().contains(AccountRole.ADMIN))) {
            throw new CitizenPhoneVerificationException();
        }
    }
}
