package com.absher.absherapp.identityrequest.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

interface SpringDataNationalIdentityRequestStatusHistoryRepository extends JpaRepository<NationalIdentityRequestStatusHistoryJpaEntity, String> {
    List<NationalIdentityRequestStatusHistoryJpaEntity> findByRequestIdOrderByChangedAtAsc(String requestId);
}
