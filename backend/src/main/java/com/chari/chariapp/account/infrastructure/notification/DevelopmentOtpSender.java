package com.chari.chariapp.account.infrastructure.notification;

import com.chari.chariapp.account.application.port.out.OtpSender;
import com.chari.chariapp.citizen.domain.PhoneReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Temporary console-based OTP delivery used while an SMS provider is not configured.
 * Replace it with a real provider before making the public API generally available.
 */
@Component
@Profile({"local", "test", "prod"})
public class DevelopmentOtpSender implements OtpSender {

    private static final Logger log = LoggerFactory.getLogger(DevelopmentOtpSender.class);

    @Override
    public void sendEnrollmentCode(PhoneReference destination, String code) {
        log.warn("Development OTP generated for phone lookup {}: {}", destination.lookup(), code);
    }
}
