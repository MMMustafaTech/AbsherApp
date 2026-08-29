package com.chari.chariapp.account.application;

import com.chari.chariapp.account.domain.EnrollmentChallengeId;

public record VerifyEnrollmentOtpCommand(EnrollmentChallengeId challengeId, String code) {
}
