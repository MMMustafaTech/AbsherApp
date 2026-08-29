package com.absher.absherapp.account.infrastructure.persistence;

import com.absher.absherapp.account.application.port.out.RefreshSessionStore;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.RefreshSession;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public class JpaRefreshSessionStore implements RefreshSessionStore {

    private final SpringDataRefreshSessionRepository repository;

    public JpaRefreshSessionStore(SpringDataRefreshSessionRepository repository) {
        this.repository = repository;
    }

    @Override
    public RefreshSession save(RefreshSession session) {
        RefreshSessionJpaEntity entity = repository.findById(session.id().value().toString())
                .orElseGet(() -> RefreshSessionJpaEntity.fromDomain(session));
        entity.apply(session);
        return repository.save(entity).toDomain();
    }

    @Override
    public Optional<RefreshSession> findByTokenHashForUpdate(String tokenHash) {
        return repository.findByTokenHash(tokenHash).map(RefreshSessionJpaEntity::toDomain);
    }

    @Override
    public int revokeAllForAccount(AccountId accountId, Instant revokedAt) {
        return repository.revokeUsableByAccountId(accountId.value().toString(), revokedAt);
    }
}
