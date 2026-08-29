package com.absher.absherapp.birthrequest.infrastructure.persistence;

import com.absher.absherapp.birthrequest.application.port.out.BirthCertificateRequestAttachmentStore;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestAttachment;
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
