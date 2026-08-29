package com.absher.absherapp.identityrequest.infrastructure.persistence;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatus;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatusChange;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "request_status_history")
public class NationalIdentityRequestStatusHistoryJpaEntity {
    @Id @Column(length = 36, columnDefinition = "CHAR(36)") private String id;
    @Column(name = "service_request_id", length = 36, nullable = false, columnDefinition = "CHAR(36)") private String requestId;
    @Enumerated(EnumType.STRING) @Column(name = "from_status", length = 32) private NationalIdentityRequestStatus fromStatus;
    @Enumerated(EnumType.STRING) @Column(name = "to_status", nullable = false, length = 32) private NationalIdentityRequestStatus toStatus;
    @Column(length = 1000) private String reason;
    @Column(name = "changed_by", length = 36, columnDefinition = "CHAR(36)") private String changedBy;
    @Column(name = "changed_at", nullable = false) private Instant changedAt;
    protected NationalIdentityRequestStatusHistoryJpaEntity() { }
    private NationalIdentityRequestStatusHistoryJpaEntity(NationalIdentityRequestStatusChange value) {
        id = value.id().toString(); requestId = value.requestId().toString(); fromStatus = value.fromStatus(); toStatus = value.toStatus();
        reason = value.reason(); changedBy = value.changedBy().value().toString(); changedAt = value.changedAt();
    }
    static NationalIdentityRequestStatusHistoryJpaEntity from(NationalIdentityRequestStatusChange value) { return new NationalIdentityRequestStatusHistoryJpaEntity(value); }
    NationalIdentityRequestStatusChange toDomain() { return new NationalIdentityRequestStatusChange(UUID.fromString(id), UUID.fromString(requestId), fromStatus, toStatus, reason, new AccountId(UUID.fromString(changedBy)), changedAt); }
}
