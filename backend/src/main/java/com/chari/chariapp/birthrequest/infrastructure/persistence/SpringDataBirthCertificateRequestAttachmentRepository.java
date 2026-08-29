package com.chari.chariapp.birthrequest.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataBirthCertificateRequestAttachmentRepository extends JpaRepository<BirthCertificateRequestAttachmentJpaEntity, String> {
    List<BirthCertificateRequestAttachmentJpaEntity> findByRequestIdOrderByUploadedAtAsc(String requestId);
    Optional<BirthCertificateRequestAttachmentJpaEntity> findByIdAndRequestId(String id, String requestId);
}
