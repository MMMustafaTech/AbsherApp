package com.chari.chariapp.identityrequest.domain;

public enum NationalIdentityRequestKind {
    ISSUANCE(false),
    RENEWAL(false),
    LOST(true),
    DAMAGED(true),
    DATA_CORRECTION(true);

    private final boolean requiresReason;

    NationalIdentityRequestKind(boolean requiresReason) {
        this.requiresReason = requiresReason;
    }

    public boolean requiresReason() {
        return requiresReason;
    }
}
