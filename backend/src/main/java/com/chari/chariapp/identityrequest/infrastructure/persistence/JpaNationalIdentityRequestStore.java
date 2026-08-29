package com.chari.chariapp.identityrequest.infrastructure.persistence;

import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.identityrequest.application.port.out.NationalIdentityRequestStore;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequest;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestStatus;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaNationalIdentityRequestStore implements NationalIdentityRequestStore {
    private final SpringDataNationalIdentityRequestRepository repository;
    public JpaNationalIdentityRequestStore(SpringDataNationalIdentityRequestRepository repository) { this.repository = repository; }
    public boolean hasOpenRequest(CitizenId citizenId) { return repository.hasOpen(citizenId.value().toString()); }
    public Optional<NationalIdentityRequest> findById(UUID requestId) { return repository.findById(requestId.toString()).filter(NationalIdentityRequestJpaEntity::isNationalIdentityRequest).map(NationalIdentityRequestJpaEntity::toDomain); }
    public Optional<NationalIdentityRequest> findByIdForUpdate(UUID requestId) { return repository.findForUpdate(requestId.toString()).filter(NationalIdentityRequestJpaEntity::isNationalIdentityRequest).map(NationalIdentityRequestJpaEntity::toDomain); }
    public NationalIdentityRequest save(NationalIdentityRequest request) { return repository.findById(request.id().toString()).map(e -> { e.apply(request); return repository.save(e); }).orElseGet(() -> repository.save(NationalIdentityRequestJpaEntity.from(request))).toDomain(); }
    public List<NationalIdentityRequest> findByStatus(NationalIdentityRequestStatus status) { return repository.findByTypeAndStatusOrderBySubmittedAtAsc(NationalIdentityRequestJpaEntity.TYPE, status).stream().map(NationalIdentityRequestJpaEntity::toDomain).toList(); }
    public List<NationalIdentityRequest> findByCitizenId(CitizenId citizenId) { return repository.findByTypeAndCitizenIdOrderBySubmittedAtDesc(NationalIdentityRequestJpaEntity.TYPE, citizenId.value().toString()).stream().map(NationalIdentityRequestJpaEntity::toDomain).toList(); }
}
