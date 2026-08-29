package com.chari.chariapp.account.domain;

/** Separates a proof used for account creation from one used to verify a phone change. */
public enum VerificationChallengePurpose {
    ACCOUNT_ENROLLMENT,
    CITIZEN_PHONE_VERIFICATION
}
