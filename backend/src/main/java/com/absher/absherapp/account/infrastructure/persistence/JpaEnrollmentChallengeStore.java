package com.absher.absherapp.account.infrastructure.persistence;

import com.absher.absherapp.account.application.port.out.EnrollmentChallengeStore;
import com.absher.absherapp.account.domain.EnrollmentChallenge;
import com.absher.absherapp.account.domain.EnrollmentChallengeId;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public class JpaEnrollmentChallengeStore implements EnrollmentChallengeStore {

    private final SpringDataEnrollmentChallengeRepository repository;

    public JpaEnrollmentChallengeStore(SpringDataEnrollmentChallengeRepository repository) {
        this.repository = repository;
    }

    @Override
    public EnrollmentChallenge save(EnrollmentChallenge challenge) {
        EnrollmentChallengeJpaEntity entity = repository.findById(challenge.id().value().toString())
                .orElseGet(() -> EnrollmentChallengeJpaEntity.fromDomain(challenge));
        entity.apply(challenge);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<EnrollmentChallenge> findById(EnrollmentChallengeId id) {
        return repository.findById(id.value().toString()).map(EnrollmentChallengeJpaEntity::toDomain);
    }

    @Override
    @Transactional
    public boolean consumeVerified(EnrollmentChallengeId id, Instant consumedAt) {
        return repository.consumeVerified(id.value().toString(), consumedAt) == 1;
    }
}
