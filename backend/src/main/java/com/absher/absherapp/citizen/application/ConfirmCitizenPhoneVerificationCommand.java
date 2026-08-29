package com.absher.absherapp.citizen.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.EnrollmentChallengeId;
import com.absher.absherapp.citizen.domain.CitizenId;

public record ConfirmCitizenPhoneVerificationCommand(
        CitizenId citizenId,
        EnrollmentChallengeId challengeId,
        String code,
        AccountId operatorId
) {
}
