package com.absher.absherapp.request.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.request.application.port.out.AttachmentContentStore;
import com.absher.absherapp.request.application.port.out.PassportRequestAttachmentStore;
import com.absher.absherapp.request.application.port.out.PassportRequestStore;
import com.absher.absherapp.request.domain.PassportRequest;
import com.absher.absherapp.request.domain.PassportRequestAttachment;
import com.absher.absherapp.request.domain.PassportRequestStatus;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Owns authorization, validation and metadata for passport-request attachments. */
public class PassportRequestAttachmentService {

    private final PassportRequestActorAccess actorAccess;
    private final PassportRequestStore requestStore;
    private final PassportRequestAttachmentStore attachmentStore;
    private final AttachmentContentStore contentStore;
    private final OperationalAuditStore auditStore;
    private final Clock clock;

    public PassportRequestAttachmentService(
            PassportRequestActorAccess actorAccess,
            PassportRequestStore requestStore,
            PassportRequestAttachmentStore attachmentStore,
            AttachmentContentStore contentStore,
            OperationalAuditStore auditStore,
            Clock clock
    ) {
        this.actorAccess = actorAccess;
        this.requestStore = requestStore;
        this.attachmentStore = attachmentStore;
        this.contentStore = contentStore;
        this.auditStore = auditStore;
        this.clock = clock;
    }

    public PassportRequestAttachment upload(AccountId actorId, UUID requestId, AttachmentUpload upload) {
        CitizenId citizenId = actorAccess.requireActiveCitizen(actorId);
        PassportRequest request = citizenOwnedRequest(citizenId, requestId);
        if (request.status() != PassportRequestStatus.SUBMITTED) {
            throw new PassportRequestConflictException("Attachments can only be added before review starts");
        }

        String contentType = AttachmentUploadPolicy.validateMetadata(upload);
        String storageKey = UUID.randomUUID().toString();
        Instant uploadedAt = Instant.now(clock);
        PassportRequestAttachment attachment = new PassportRequestAttachment(
                UUID.randomUUID(), requestId, storageKey, AttachmentUploadPolicy.safeFileName(upload.originalFileName()),
                contentType, upload.sizeBytes(), actorId, uploadedAt
        );

        try (BufferedInputStream content = new BufferedInputStream(upload.content())) {
            AttachmentUploadPolicy.validateSignature(content, contentType);
            contentStore.store(storageKey, content);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read attachment", exception);
        }

        try {
            PassportRequestAttachment saved = attachmentStore.save(attachment);
            auditStore.record(actorId.value().toString(), "PASSPORT_REQUEST_ATTACHMENT_UPLOADED",
                    "SERVICE_REQUEST", requestId.toString(), null, uploadedAt);
            return saved;
        } catch (RuntimeException exception) {
            contentStore.delete(storageKey);
            throw exception;
        }
    }

    public List<PassportRequestAttachment> listMine(AccountId actorId, UUID requestId) {
        CitizenId citizenId = actorAccess.requireActiveCitizen(actorId);
        citizenOwnedRequest(citizenId, requestId);
        return attachmentStore.findByRequestId(requestId);
    }

    public AttachmentContent downloadMine(AccountId actorId, UUID requestId, UUID attachmentId) {
        CitizenId citizenId = actorAccess.requireActiveCitizen(actorId);
        citizenOwnedRequest(citizenId, requestId);
        return content(requestId, attachmentId);
    }

    public List<PassportRequestAttachment> listForOperations(AccountId actorId, UUID requestId) {
        actorAccess.requireActiveOperator(actorId);
        requireRequest(requestId);
        return attachmentStore.findByRequestId(requestId);
    }

    public AttachmentContent downloadForOperations(AccountId actorId, UUID requestId, UUID attachmentId) {
        actorAccess.requireActiveOperator(actorId);
        requireRequest(requestId);
        return content(requestId, attachmentId);
    }

    private PassportRequest citizenOwnedRequest(CitizenId citizenId, UUID requestId) {
        PassportRequest request = requireRequest(requestId);
        if (!request.belongsTo(citizenId)) {
            throw new NotFoundException("Request not found");
        }
        return request;
    }

    private PassportRequest requireRequest(UUID requestId) {
        return requestStore.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));
    }

    private AttachmentContent content(UUID requestId, UUID attachmentId) {
        PassportRequestAttachment attachment = attachmentStore.findByIdAndRequestId(attachmentId, requestId)
                .orElseThrow(() -> new NotFoundException("Attachment not found"));
        return new AttachmentContent(attachment, contentStore.read(attachment.storageKey()));
    }

}
