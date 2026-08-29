package com.absher.absherapp.account.application;

public class EnrollmentUnavailableException extends RuntimeException {

    public EnrollmentUnavailableException() {
        super("Enrollment verification is not available for this identity");
    }
}
