package com.chari.chariapp.birthrequest.infrastructure.persistence;
import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.birthrequest.domain.*;
import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;
@Entity @Table(name="request_status_history") public class BirthCertificateRequestStatusHistoryJpaEntity {
    @Id @Column(length=36,columnDefinition="CHAR(36)") private String id;
    @Column(name="service_request_id",length=36,nullable=false,columnDefinition="CHAR(36)") private String requestId;
    @Enumerated(EnumType.STRING) @Column(name="from_status",length=32) private BirthCertificateRequestStatus fromStatus;
    @Enumerated(EnumType.STRING) @Column(name="to_status",nullable=false,length=32) private BirthCertificateRequestStatus toStatus;
    @Column(length=1000) private String reason;
    @Column(name="changed_by",length=36,columnDefinition="CHAR(36)") private String changedBy;
    @Column(name="changed_at",nullable=false) private Instant changedAt;
    protected BirthCertificateRequestStatusHistoryJpaEntity(){}
    private BirthCertificateRequestStatusHistoryJpaEntity(BirthCertificateRequestStatusChange v){id=v.id().toString();requestId=v.requestId().toString();fromStatus=v.fromStatus();toStatus=v.toStatus();reason=v.reason();changedBy=v.changedBy().value().toString();changedAt=v.changedAt();}
    static BirthCertificateRequestStatusHistoryJpaEntity from(BirthCertificateRequestStatusChange v){return new BirthCertificateRequestStatusHistoryJpaEntity(v);}
    BirthCertificateRequestStatusChange toDomain(){return new BirthCertificateRequestStatusChange(UUID.fromString(id),UUID.fromString(requestId),fromStatus,toStatus,reason,new AccountId(UUID.fromString(changedBy)),changedAt);}
}
