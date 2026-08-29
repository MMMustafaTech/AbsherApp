package com.absher.absherapp.account.application;

import com.absher.absherapp.account.domain.EnrollmentChallengeId;

public record VerifyEnrollmentOtpCommand(EnrollmentChallengeId challengeId, String code) {
}
