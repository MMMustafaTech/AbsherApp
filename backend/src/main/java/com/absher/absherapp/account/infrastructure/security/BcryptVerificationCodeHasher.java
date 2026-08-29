package com.absher.absherapp.account.infrastructure.security;

import com.absher.absherapp.account.application.port.out.VerificationCodeHasher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptVerificationCodeHasher implements VerificationCodeHasher {

    private final PasswordEncoder passwordEncoder;

    public BcryptVerificationCodeHasher(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public String hash(String code) {
        return passwordEncoder.encode(code);
    }

    @Override
    public boolean matches(String code, String hash) {
        return passwordEncoder.matches(code, hash);
    }
}
