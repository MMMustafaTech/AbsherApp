package com.chari.chariapp.request.infrastructure.persistence;

import com.chari.chariapp.request.application.port.out.PassportRequestStatusHistoryStore;
import com.chari.chariapp.request.domain.PassportRequestStatusChange;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public class JpaPassportRequestStatusHistoryStore implements PassportRequestStatusHistoryStore {
    private final SpringDataPassportRequestStatusHistoryRepository repository;

    public JpaPassportRequestStatusHistoryStore(SpringDataPassportRequestStatusHistoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public void append(PassportRequestStatusChange change) {
        repository.save(PassportRequestStatusHistoryJpaEntity.from(change));
    }

    @Override
    public List<PassportRequestStatusChange> findByRequestId(UUID requestId) {
        return repository.findByRequestIdOrderByChangedAtAsc(requestId.toString()).stream()
                .map(PassportRequestStatusHistoryJpaEntity::toDomain)
                .toList();
    }
}
