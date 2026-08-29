package com.chari.chariapp.shared.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataOperationalAuditEventRepository extends JpaRepository<OperationalAuditEventJpaEntity, String> {
}
