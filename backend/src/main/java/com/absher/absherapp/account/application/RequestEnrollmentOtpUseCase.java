package com.absher.absherapp.account.application;

import com.absher.absherapp.account.domain.EnrollmentChallengeId;

public interface RequestEnrollmentOtpUseCase {

    EnrollmentChallengeId request(RequestEnrollmentOtpCommand command);
}
