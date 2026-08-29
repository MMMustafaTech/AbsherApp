package com.chari.chariapp.request.infrastructure.persistence;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.request.domain.PassportRequestStatus;
import com.chari.chariapp.request.domain.PassportRequestStatusChange;
import jakarta.persistence.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "request_status_history")
public class PassportRequestStatusHistoryJpaEntity {
    @Id
    @Column(length = 36, columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "service_request_id", length = 36, nullable = false, columnDefinition = "CHAR(36)")
    private String requestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "from_status", length = 32)
    private PassportRequestStatus fromStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "to_status", nullable = false, length = 32)
    private PassportRequestStatus toStatus;

    @Column(length = 1000)
    private String reason;

    @Column(name = "changed_by", length = 36, columnDefinition = "CHAR(36)")
    private String changedBy;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    protected PassportRequestStatusHistoryJpaEntity() {
    }

    private PassportRequestStatusHistoryJpaEntity(PassportRequestStatusChange value) {
        id = value.id().toString();
        requestId = value.requestId().toString();
        fromStatus = value.fromStatus();
        toStatus = value.toStatus();
        reason = value.reason();
        changedBy = value.changedBy().value().toString();
        changedAt = value.changedAt();
    }

    static PassportRequestStatusHistoryJpaEntity from(PassportRequestStatusChange value) {
        return new PassportRequestStatusHistoryJpaEntity(value);
    }

    PassportRequestStatusChange toDomain() {
        return new PassportRequestStatusChange(
                UUID.fromString(id),
                UUID.fromString(requestId),
                fromStatus,
                toStatus,
                reason,
                new AccountId(UUID.fromString(changedBy)),
                changedAt
        );
    }
}
