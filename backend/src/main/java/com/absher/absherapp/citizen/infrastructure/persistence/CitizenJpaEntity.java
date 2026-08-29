package com.absher.absherapp.citizen.infrastructure.persistence;

import com.absher.absherapp.citizen.domain.Citizen;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.citizen.domain.NationalIdReference;
import com.absher.absherapp.citizen.domain.PhoneReference;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "citizen_registry")
public class CitizenJpaEntity {

    @Id
    @Column(length = 36, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "national_id_lookup", length = 64, nullable = false, unique = true, columnDefinition = "CHAR(64)")
    private String nationalIdLookup;

    @Column(name = "national_id_ciphertext", nullable = false, columnDefinition = "TEXT")
    private String nationalIdCiphertext;

    @Column(name = "phone_lookup", length = 64, unique = true, columnDefinition = "CHAR(64)")
    private String phoneLookup;

    @Column(name = "phone_ciphertext", columnDefinition = "TEXT")
    private String phoneCiphertext;

    @Column(name = "phone_verified_at")
    private Instant phoneVerifiedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    protected CitizenJpaEntity() {
    }

    private CitizenJpaEntity(Citizen citizen) {
        this.id = citizen.id().value().toString();
        this.nationalIdLookup = citizen.nationalId().lookup();
        this.nationalIdCiphertext = citizen.nationalId().ciphertext();
        this.phoneLookup = citizen.verifiedPhoneOptional().map(PhoneReference::lookup).orElse(null);
        this.phoneCiphertext = citizen.verifiedPhoneOptional().map(PhoneReference::ciphertext).orElse(null);
        this.phoneVerifiedAt = citizen.phoneVerifiedAt();
        this.createdAt = citizen.createdAt();
        this.updatedAt = citizen.createdAt();
    }

    public static CitizenJpaEntity fromDomain(Citizen citizen) {
        return new CitizenJpaEntity(citizen);
    }

    public Citizen toDomain() {
        return new Citizen(
                new CitizenId(UUID.fromString(id)),
                new NationalIdReference(nationalIdLookup, nationalIdCiphertext),
                phoneLookup == null ? null : new PhoneReference(phoneLookup, phoneCiphertext),
                phoneVerifiedAt,
                createdAt
        );
    }

    void replaceVerifiedPhone(PhoneReference phone, Instant verifiedAt) {
        this.phoneLookup = phone.lookup();
        this.phoneCiphertext = phone.ciphertext();
        this.phoneVerifiedAt = verifiedAt;
    }

    @PrePersist
    void initializeTimestamps() {
        Instant now = Instant.now();
        if (createdAt == null) {
            createdAt = now;
        }
        if (updatedAt == null) {
            updatedAt = createdAt;
        }
    }

    @jakarta.persistence.PreUpdate
    void updateTimestamp() {
        updatedAt = Instant.now();
    }
}
