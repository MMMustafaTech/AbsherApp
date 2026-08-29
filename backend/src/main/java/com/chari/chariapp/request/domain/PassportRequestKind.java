package com.chari.chariapp.request.domain;

/** The service the citizen is asking for; this is distinct from the request workflow status. */
public enum PassportRequestKind {
    ISSUANCE(false),
    RENEWAL(false),
    LOST(true),
    DAMAGED(true),
    DATA_CORRECTION(true);

    private final boolean requiresReason;

    PassportRequestKind(boolean requiresReason) {
        this.requiresReason = requiresReason;
    }

    public boolean requiresReason() {
        return requiresReason;
    }
}
