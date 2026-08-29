package com.chari.chariapp.birthrequest.domain;

public enum BirthCertificateRequestStatus {
    SUBMITTED, UNDER_REVIEW, APPROVED, REJECTED;
    public boolean isOpen() { return this == SUBMITTED || this == UNDER_REVIEW; }
}
