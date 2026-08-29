package com.absher.absherapp.account.application;

public class InvalidEnrollmentOtpException extends RuntimeException {

    public InvalidEnrollmentOtpException() {
        super("Verification code is invalid, expired, or unavailable");
    }
}
