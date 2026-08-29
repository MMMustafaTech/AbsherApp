package com.chari.chariapp.citizen.application;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.account.domain.EnrollmentChallengeId;
import com.chari.chariapp.citizen.domain.CitizenId;

public record ConfirmCitizenPhoneVerificationCommand(
        CitizenId citizenId,
        EnrollmentChallengeId challengeId,
        String code,
        AccountId operatorId
) {
}
