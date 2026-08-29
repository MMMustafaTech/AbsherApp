package com.absher.absherapp.identityrequest.infrastructure.persistence;

import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStatusHistoryStore;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatusChange;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JpaNationalIdentityRequestStatusHistoryStore implements NationalIdentityRequestStatusHistoryStore {
    private final SpringDataNationalIdentityRequestStatusHistoryRepository repository;
    public JpaNationalIdentityRequestStatusHistoryStore(SpringDataNationalIdentityRequestStatusHistoryRepository repository) { this.repository = repository; }
    public void append(NationalIdentityRequestStatusChange change) { repository.save(NationalIdentityRequestStatusHistoryJpaEntity.from(change)); }
    public List<NationalIdentityRequestStatusChange> findByRequestId(UUID requestId) { return repository.findByRequestIdOrderByChangedAtAsc(requestId.toString()).stream().map(NationalIdentityRequestStatusHistoryJpaEntity::toDomain).toList(); }
}
