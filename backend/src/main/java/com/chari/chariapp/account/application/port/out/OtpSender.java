package com.chari.chariapp.account.application.port.out;

import com.chari.chariapp.citizen.domain.PhoneReference;

public interface OtpSender {

    void sendEnrollmentCode(PhoneReference destination, String code);
}
