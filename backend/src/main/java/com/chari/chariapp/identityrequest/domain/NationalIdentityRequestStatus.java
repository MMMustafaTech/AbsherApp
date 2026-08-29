package com.chari.chariapp.identityrequest.domain;

public enum NationalIdentityRequestStatus {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED;

    public boolean isOpen() {
        return this == SUBMITTED || this == UNDER_REVIEW;
    }
}
