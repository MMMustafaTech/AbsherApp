package com.absher.absherapp.request.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

interface SpringDataPassportRequestAttachmentRepository extends JpaRepository<PassportRequestAttachmentJpaEntity, String> {
    List<PassportRequestAttachmentJpaEntity> findByRequestIdOrderByUploadedAtAsc(String requestId);

    Optional<PassportRequestAttachmentJpaEntity> findByIdAndRequestId(String id, String requestId);
}
