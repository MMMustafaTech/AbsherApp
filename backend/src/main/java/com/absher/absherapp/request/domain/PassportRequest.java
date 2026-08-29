package com.absher.absherapp.request.domain;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.citizen.domain.CitizenId;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record PassportRequest(
        UUID id,
        CitizenId citizenId,
        PassportRequestKind kind,
        String requestReason,
        PassportRequestStatus status,
        AccountId reviewedBy,
        Instant submittedAt,
        Instant reviewedAt,
        String decisionReason
) {

    public PassportRequest {
        Objects.requireNonNull(id, "Request ID is required");
        Objects.requireNonNull(citizenId, "Citizen ID is required");
        Objects.requireNonNull(kind, "Request kind is required");
        Objects.requireNonNull(status, "Request status is required");
        Objects.requireNonNull(submittedAt, "Submission time is required");

        requestReason = normalizeReason(requestReason);
        if (requestReason != null && requestReason.length() > 1000) {
            throw new IllegalArgumentException("Request reason must not exceed 1000 characters");
        }
        if (kind.requiresReason() && requestReason == null) {
            throw new IllegalArgumentException("This passport request type requires a reason");
        }
        decisionReason = normalizeReason(decisionReason);
        if (status == PassportRequestStatus.SUBMITTED && (reviewedBy != null || reviewedAt != null || decisionReason != null)) {
            throw new IllegalArgumentException("A submitted request cannot contain review data");
        }
        if (status != PassportRequestStatus.SUBMITTED && (reviewedBy == null || reviewedAt == null)) {
            throw new IllegalArgumentException("A reviewed request must identify its reviewer and review time");
        }
        if (status == PassportRequestStatus.UNDER_REVIEW && decisionReason != null) {
            throw new IllegalArgumentException("An undecided request cannot contain a decision reason");
        }
        if (status == PassportRequestStatus.REJECTED && decisionReason == null) {
            throw new IllegalArgumentException("A rejected request requires a reason");
        }
    }

    /** Compatibility constructor for existing issuance requests. */
    public PassportRequest(
            UUID id,
            CitizenId citizenId,
            PassportRequestStatus status,
            AccountId reviewedBy,
            Instant submittedAt,
            Instant reviewedAt,
            String decisionReason
    ) {
        this(id, citizenId, PassportRequestKind.ISSUANCE, null, status, reviewedBy, submittedAt, reviewedAt, decisionReason);
    }

    public static PassportRequest submitted(CitizenId citizenId, Instant now) {
        return submitted(citizenId, PassportRequestKind.ISSUANCE, null, now);
    }

    public static PassportRequest submitted(CitizenId citizenId, PassportRequestKind kind, String requestReason, Instant now) {
        return new PassportRequest(UUID.randomUUID(), citizenId, kind, requestReason, PassportRequestStatus.SUBMITTED, null, now, null, null);
    }

    public PassportRequest startReview(AccountId operator, Instant now) {
        if (status != PassportRequestStatus.SUBMITTED) {
            throw new PassportRequestTransitionException("Request is not awaiting review");
        }
        return new PassportRequest(id, citizenId, kind, requestReason, PassportRequestStatus.UNDER_REVIEW, operator, submittedAt, now, null);
    }

    public PassportRequest decide(AccountId operator, boolean approved, String reason, Instant now) {
        if (status != PassportRequestStatus.UNDER_REVIEW) {
            throw new PassportRequestTransitionException("Request is not under review");
        }
        if (!operator.equals(reviewedBy)) {
            throw new PassportRequestTransitionException("Only the reviewing employee may decide this request");
        }
        if (!approved && normalizeReason(reason) == null) {
            throw new PassportRequestTransitionException("A rejection reason is required");
        }
        return new PassportRequest(id, citizenId, kind, requestReason, approved ? PassportRequestStatus.APPROVED : PassportRequestStatus.REJECTED, operator, submittedAt, now, reason);
    }

    public boolean belongsTo(CitizenId candidateCitizenId) {
        return citizenId.equals(candidateCitizenId);
    }

    private static String normalizeReason(String reason) {
        if (reason == null || reason.isBlank()) {
            return null;
        }
        return reason.trim();
    }
}
