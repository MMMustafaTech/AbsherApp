package com.absher.absherapp.account.infrastructure.security;

import com.absher.absherapp.account.application.port.out.OtpCodeGenerator;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

@Component
public class SecureSixDigitOtpCodeGenerator implements OtpCodeGenerator {

    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public String generate() {
        return "%06d".formatted(secureRandom.nextInt(1_000_000));
    }
}
