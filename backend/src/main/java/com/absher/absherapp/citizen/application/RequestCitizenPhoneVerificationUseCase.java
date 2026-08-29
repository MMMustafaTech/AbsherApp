package com.absher.absherapp.citizen.application;

import com.absher.absherapp.account.domain.EnrollmentChallengeId;

public interface RequestCitizenPhoneVerificationUseCase {
    EnrollmentChallengeId request(RequestCitizenPhoneVerificationCommand command);
}
