package com.chari.chariapp.account.infrastructure.persistence;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.account.domain.RefreshSession;
import com.chari.chariapp.account.domain.RefreshSessionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_sessions")
public class RefreshSessionJpaEntity {

    @Id
    @Column(length = 36, nullable = false, updatable = false, columnDefinition = "CHAR(36)")
    private String id;

    @Column(name = "account_id", length = 36, nullable = false, columnDefinition = "CHAR(36)")
    private String accountId;

    @Column(name = "token_hash", length = 64, nullable = false, unique = true, columnDefinition = "CHAR(64)")
    private String tokenHash;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked_at")
    private Instant revokedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Version
    private Long version;

    protected RefreshSessionJpaEntity() {
    }

    private RefreshSessionJpaEntity(RefreshSession session) {
        this.id = session.id().value().toString();
        this.accountId = session.accountId().value().toString();
        this.tokenHash = session.tokenHash();
        this.expiresAt = session.expiresAt();
        this.revokedAt = session.revokedAt();
        this.createdAt = session.createdAt();
    }

    public static RefreshSessionJpaEntity fromDomain(RefreshSession session) {
        return new RefreshSessionJpaEntity(session);
    }

    public void apply(RefreshSession session) {
        this.expiresAt = session.expiresAt();
        this.revokedAt = session.revokedAt();
    }

    public RefreshSession toDomain() {
        return new RefreshSession(
                new RefreshSessionId(UUID.fromString(id)),
                new AccountId(UUID.fromString(accountId)),
                tokenHash,
                createdAt,
                expiresAt,
                revokedAt
        );
    }
}
