package com.chari.chariapp.birthrequest.domain;
import com.chari.chariapp.account.domain.AccountId;
import java.time.Instant;
import java.util.UUID;
public record BirthCertificateRequestStatusChange(UUID id, UUID requestId, BirthCertificateRequestStatus fromStatus, BirthCertificateRequestStatus toStatus, String reason, AccountId changedBy, Instant changedAt) { }
