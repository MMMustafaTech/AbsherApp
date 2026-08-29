package com.absher.absherapp.identityrequest.infrastructure.persistence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataNationalIdentityRequestAttachmentRepository extends JpaRepository<NationalIdentityRequestAttachmentJpaEntity,String> {
    List<NationalIdentityRequestAttachmentJpaEntity> findByRequestIdOrderByUploadedAtAsc(String requestId);
    Optional<NationalIdentityRequestAttachmentJpaEntity> findByIdAndRequestId(String id,String requestId);
}
