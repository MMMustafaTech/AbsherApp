package com.chari.chariapp.account.application;

public class EnrollmentUnavailableException extends RuntimeException {

    public EnrollmentUnavailableException() {
        super("Enrollment verification is not available for this identity");
    }
}
