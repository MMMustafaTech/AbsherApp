package com.absher.absherapp.account.infrastructure.persistence;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;

public interface SpringDataEnrollmentChallengeRepository extends JpaRepository<EnrollmentChallengeJpaEntity, String> {

    @Modifying
    @Query("""
            update EnrollmentChallengeJpaEntity challenge
               set challenge.enrollmentConsumedAt = :consumedAt
             where challenge.id = :id
               and challenge.verifiedAt is not null
               and challenge.enrollmentConsumedAt is null
               and challenge.expiresAt > :consumedAt
            """)
    int consumeVerified(@Param("id") String id, @Param("consumedAt") Instant consumedAt);
}
