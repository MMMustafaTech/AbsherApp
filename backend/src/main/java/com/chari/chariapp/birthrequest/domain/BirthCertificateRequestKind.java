package com.chari.chariapp.birthrequest.domain;

public enum BirthCertificateRequestKind {
    NEWBORN_REGISTRATION(false),
    CERTIFICATE_EXTRACT(false),
    DATA_CORRECTION(true);

    private final boolean requiresReason;
    BirthCertificateRequestKind(boolean requiresReason) { this.requiresReason = requiresReason; }
    public boolean requiresReason() { return requiresReason; }
}
