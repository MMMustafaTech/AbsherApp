package com.chari.chariapp.request.infrastructure.persistence;

import com.chari.chariapp.request.application.port.out.PassportRequestAttachmentStore;
import com.chari.chariapp.request.domain.PassportRequestAttachment;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaPassportRequestAttachmentStore implements PassportRequestAttachmentStore {
    private final SpringDataPassportRequestAttachmentRepository repository;

    public JpaPassportRequestAttachmentStore(SpringDataPassportRequestAttachmentRepository repository) {
        this.repository = repository;
    }

    @Override
    public PassportRequestAttachment save(PassportRequestAttachment attachment) {
        return repository.save(PassportRequestAttachmentJpaEntity.from(attachment)).toDomain();
    }

    @Override
    public List<PassportRequestAttachment> findByRequestId(UUID requestId) {
        return repository.findByRequestIdOrderByUploadedAtAsc(requestId.toString()).stream()
                .map(PassportRequestAttachmentJpaEntity::toDomain)
                .toList();
    }

    @Override
    public Optional<PassportRequestAttachment> findByIdAndRequestId(UUID attachmentId, UUID requestId) {
        return repository.findByIdAndRequestId(attachmentId.toString(), requestId.toString())
                .map(PassportRequestAttachmentJpaEntity::toDomain);
    }
}
