package com.chari.chariapp.account.infrastructure.persistence;

import jakarta.persistence.LockModeType;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface SpringDataRefreshSessionRepository extends JpaRepository<RefreshSessionJpaEntity, String> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<RefreshSessionJpaEntity> findByTokenHash(String tokenHash);

    @Modifying
    @Transactional
    @Query("update RefreshSessionJpaEntity session set session.revokedAt = :revokedAt "
            + "where session.accountId = :accountId and session.revokedAt is null and session.expiresAt > :revokedAt")
    int revokeUsableByAccountId(@Param("accountId") String accountId, @Param("revokedAt") Instant revokedAt);
}
