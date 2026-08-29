package com.chari.chariapp.birthrequest.infrastructure.persistence;

import com.chari.chariapp.birthrequest.application.port.out.BirthCertificateRequestAttachmentStore;
import com.chari.chariapp.birthrequest.domain.BirthCertificateRequestAttachment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaBirthCertificateRequestAttachmentStore implements BirthCertificateRequestAttachmentStore {
    private final SpringDataBirthCertificateRequestAttachmentRepository repository;
    public JpaBirthCertificateRequestAttachmentStore(SpringDataBirthCertificateRequestAttachmentRepository repository) { this.repository = repository; }
    public BirthCertificateRequestAttachment save(BirthCertificateRequestAttachment attachment) { return repository.save(BirthCertificateRequestAttachmentJpaEntity.from(attachment)).toDomain(); }
    public List<BirthCertificateRequestAttachment> findByRequestId(UUID requestId) { return repository.findByRequestIdOrderByUploadedAtAsc(requestId.toString()).stream().map(BirthCertificateRequestAttachmentJpaEntity::toDomain).toList(); }
    public Optional<BirthCertificateRequestAttachment> findByIdAndRequestId(UUID attachmentId, UUID requestId) { return repository.findByIdAndRequestId(attachmentId.toString(), requestId.toString()).map(BirthCertificateRequestAttachmentJpaEntity::toDomain); }
}
