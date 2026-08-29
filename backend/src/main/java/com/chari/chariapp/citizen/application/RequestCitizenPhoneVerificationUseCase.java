package com.chari.chariapp.citizen.application;

import com.chari.chariapp.account.domain.EnrollmentChallengeId;

public interface RequestCitizenPhoneVerificationUseCase {
    EnrollmentChallengeId request(RequestCitizenPhoneVerificationCommand command);
}
