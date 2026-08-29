package com.absher.absherapp.account.application.port.out;

import com.absher.absherapp.citizen.domain.PhoneReference;

public interface OtpSender {

    void sendEnrollmentCode(PhoneReference destination, String code);
}
