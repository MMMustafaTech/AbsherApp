package com.absher.absherapp.citizen.application;

/** Keeps phone-verification failures from disclosing operational details. */
public class CitizenPhoneVerificationException extends RuntimeException {
    public CitizenPhoneVerificationException() {
        super("Citizen phone verification is unavailable");
    }
}
