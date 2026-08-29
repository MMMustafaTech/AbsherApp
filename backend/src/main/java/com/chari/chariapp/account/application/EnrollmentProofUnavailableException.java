package com.chari.chariapp.account.application;

public class EnrollmentProofUnavailableException extends RuntimeException {

    public EnrollmentProofUnavailableException() {
        super("Enrollment verification is invalid, expired, or has already been used");
    }
}
