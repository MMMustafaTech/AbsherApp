package com.chari.chariapp.identityrequest.infrastructure.persistence;

import com.chari.chariapp.identityrequest.application.port.out.NationalIdentityRequestAttachmentStore;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestAttachment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository public class JpaNationalIdentityRequestAttachmentStore implements NationalIdentityRequestAttachmentStore {
    private final SpringDataNationalIdentityRequestAttachmentRepository repository;
    public JpaNationalIdentityRequestAttachmentStore(SpringDataNationalIdentityRequestAttachmentRepository repository) { this.repository=repository; }
    public NationalIdentityRequestAttachment save(NationalIdentityRequestAttachment attachment) { return repository.save(NationalIdentityRequestAttachmentJpaEntity.from(attachment)).toDomain(); }
    public List<NationalIdentityRequestAttachment> findByRequestId(UUID requestId) { return repository.findByRequestIdOrderByUploadedAtAsc(requestId.toString()).stream().map(NationalIdentityRequestAttachmentJpaEntity::toDomain).toList(); }
    public Optional<NationalIdentityRequestAttachment> findByIdAndRequestId(UUID attachmentId,UUID requestId) { return repository.findByIdAndRequestId(attachmentId.toString(),requestId.toString()).map(NationalIdentityRequestAttachmentJpaEntity::toDomain); }
}
