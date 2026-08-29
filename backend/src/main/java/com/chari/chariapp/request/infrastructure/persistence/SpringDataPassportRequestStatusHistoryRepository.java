package com.chari.chariapp.request.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataPassportRequestStatusHistoryRepository extends JpaRepository<PassportRequestStatusHistoryJpaEntity, String> {
    List<PassportRequestStatusHistoryJpaEntity> findByRequestIdOrderByChangedAtAsc(String requestId);
}
