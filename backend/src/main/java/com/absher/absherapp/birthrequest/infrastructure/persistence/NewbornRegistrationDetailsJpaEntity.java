package com.absher.absherapp.birthrequest.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

@Entity
@Table(name = "birth_certificate_registration_details")
class NewbornRegistrationDetailsJpaEntity {

    @Id
    @Column(name = "service_request_id", length = 36, columnDefinition = "CHAR(36)")
    private String requestId;

    @Column(name = "encrypted_payload", nullable = false, columnDefinition = "TEXT")
    private String encryptedPayload;

    @Version
    private Long version;

    protected NewbornRegistrationDetailsJpaEntity() {
    }

    NewbornRegistrationDetailsJpaEntity(String requestId, String encryptedPayload) {
        this.requestId = requestId;
        this.encryptedPayload = encryptedPayload;
    }

    String encryptedPayload() {
        return encryptedPayload;
    }
}
