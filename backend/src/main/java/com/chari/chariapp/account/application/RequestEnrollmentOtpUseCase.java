package com.chari.chariapp.account.application;

import com.chari.chariapp.account.domain.EnrollmentChallengeId;

public interface RequestEnrollmentOtpUseCase {

    EnrollmentChallengeId request(RequestEnrollmentOtpCommand command);
}
