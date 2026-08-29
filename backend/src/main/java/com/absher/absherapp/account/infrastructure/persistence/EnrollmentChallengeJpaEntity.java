package com.absher.absherapp.account.infrastructure.persistence;

import com.absher.absherapp.account.domain.EnrollmentChallenge;
import com.absher.absherapp.account.domain.EnrollmentChallengeId;
import com.absher.absherapp.account.domain.VerificationChallengePurpose;
import com.absher.absherapp.citizen.domain.CitizenId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "verification_challenges")
public class EnrollmentChallengeJpaEntity {

    @Id
    @Column(length = 36, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "citizen_id", length = 36, nullable = false, columnDefinition = "CHAR(36)")
    private String citizenId;

    @Column(nullable = false, length = 32)
    private String channel;

    @Column(nullable = false, length = 32)
    private String purpose;

    @Column(name = "destination_lookup", length = 64, nullable = false, columnDefinition = "CHAR(64)")
    private String destinationLookup;

    @Column(name = "destination_ciphertext", columnDefinition = "TEXT")
    private String destinationCiphertext;

    @Column(name = "challenge_hash", length = 64, nullable = false, columnDefinition = "CHAR(64)")
    private String challengeHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "failed_attempts", nullable = false)
    private int failedAttempts;

    @Column(name = "verified_at")
    private Instant verifiedAt;

    @Column(name = "enrollment_consumed_at")
    private Instant enrollmentConsumedAt;

    @Version
    private Long version;

    protected EnrollmentChallengeJpaEntity() {
    }

    private EnrollmentChallengeJpaEntity(EnrollmentChallenge challenge) {
        this.id = challenge.id().value().toString();
        this.citizenId = challenge.citizenId().value().toString();
        this.channel = "SMS";
        this.purpose = challenge.purpose().name();
        this.destinationLookup = challenge.destinationLookup();
        this.destinationCiphertext = challenge.destinationCiphertext();
        this.challengeHash = challenge.codeHash();
        this.expiresAt = challenge.expiresAt();
        this.failedAttempts = challenge.failedAttempts();
        this.verifiedAt = challenge.verifiedAt();
        this.enrollmentConsumedAt = challenge.enrollmentConsumedAt();
    }

    public static EnrollmentChallengeJpaEntity fromDomain(EnrollmentChallenge challenge) {
        return new EnrollmentChallengeJpaEntity(challenge);
    }

    public void apply(EnrollmentChallenge challenge) {
        this.citizenId = challenge.citizenId().value().toString();
        this.purpose = challenge.purpose().name();
        this.destinationLookup = challenge.destinationLookup();
        this.destinationCiphertext = challenge.destinationCiphertext();
        this.challengeHash = challenge.codeHash();
        this.expiresAt = challenge.expiresAt();
        this.failedAttempts = challenge.failedAttempts();
        this.verifiedAt = challenge.verifiedAt();
        this.enrollmentConsumedAt = challenge.enrollmentConsumedAt();
    }

    public EnrollmentChallenge toDomain() {
        return new EnrollmentChallenge(
                new EnrollmentChallengeId(UUID.fromString(id)),
                new CitizenId(UUID.fromString(citizenId)),
                VerificationChallengePurpose.valueOf(purpose),
                destinationLookup,
                destinationCiphertext,
                challengeHash,
                expiresAt,
                failedAttempts,
                verifiedAt,
                enrollmentConsumedAt
        );
    }
}
