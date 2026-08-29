package com.absher.absherapp.account.infrastructure.notification;

import com.absher.absherapp.account.application.port.out.OtpSender;
import com.absher.absherapp.citizen.domain.PhoneReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"local", "test"})
public class DevelopmentOtpSender implements OtpSender {

    private static final Logger log = LoggerFactory.getLogger(DevelopmentOtpSender.class);

    @Override
    public void sendEnrollmentCode(PhoneReference destination, String code) {
        log.warn("Development OTP generated for phone lookup {}: {}", destination.lookup(), code);
    }
}
