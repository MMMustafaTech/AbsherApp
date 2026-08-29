package com.absher.absherapp.account.application;

import com.absher.absherapp.account.application.port.out.EnrollmentChallengeStore;
import com.absher.absherapp.account.application.port.out.VerificationCodeHasher;
import com.absher.absherapp.account.domain.EnrollmentChallenge;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public class VerifyEnrollmentOtpService implements VerifyEnrollmentOtpUseCase {

    private final EnrollmentChallengeStore challengeStore;
    private final VerificationCodeHasher codeHasher;
    private final Clock clock;

    public VerifyEnrollmentOtpService(
            EnrollmentChallengeStore challengeStore,
            VerificationCodeHasher codeHasher,
            Clock clock
    ) {
        this.challengeStore = Objects.requireNonNull(challengeStore, "Challenge store is required");
        this.codeHasher = Objects.requireNonNull(codeHasher, "Verification code hasher is required");
        this.clock = Objects.requireNonNull(clock, "Clock is required");
    }

    @Override
    public void verify(VerifyEnrollmentOtpCommand command) {
        Objects.requireNonNull(command, "Verify OTP command is required");
        EnrollmentChallenge challenge = challengeStore.findById(command.challengeId())
                .orElseThrow(InvalidEnrollmentOtpException::new);
        Instant now = Instant.now(clock);

        if (challenge.isExpired(now) || challenge.isLocked() || challenge.isVerified()) {
            throw new InvalidEnrollmentOtpException();
        }
        if (!codeHasher.matches(command.code(), challenge.codeHash())) {
            challengeStore.save(challenge.registerFailedAttempt());
            throw new InvalidEnrollmentOtpException();
        }
        challengeStore.save(challenge.markVerified(now));
    }
}
