package com.absher.absherapp.account.application.port.out;

public interface VerificationCodeHasher {

    String hash(String code);

    boolean matches(String code, String hash);
}
