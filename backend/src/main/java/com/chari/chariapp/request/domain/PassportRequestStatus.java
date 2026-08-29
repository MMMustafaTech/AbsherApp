package com.chari.chariapp.request.domain;

public enum PassportRequestStatus {
    SUBMITTED,
    UNDER_REVIEW,
    APPROVED,
    REJECTED;

    public boolean isOpen() {
        return this == SUBMITTED || this == UNDER_REVIEW;
    }
}
