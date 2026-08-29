package com.absher.absherapp.birthrequest.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.birthrequest.application.port.out.BirthCertificateRequestAttachmentStore;
import com.absher.absherapp.birthrequest.application.port.out.BirthCertificateRequestStore;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequest;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestAttachment;
import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestStatus;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.request.application.AttachmentUpload;
import com.absher.absherapp.request.application.AttachmentUploadPolicy;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.request.application.port.out.AttachmentContentStore;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class BirthCertificateRequestAttachmentService {
    private final PassportRequestActorAccess access;
    private final BirthCertificateRequestStore requests;
    private final BirthCertificateRequestAttachmentStore attachments;
    private final AttachmentContentStore contentStore;
    private final OperationalAuditStore audit;
    private final Clock clock;

    public BirthCertificateRequestAttachmentService(PassportRequestActorAccess access, BirthCertificateRequestStore requests,
                                                    BirthCertificateRequestAttachmentStore attachments, AttachmentContentStore contentStore,
                                                    OperationalAuditStore audit, Clock clock) {
        this.access = access; this.requests = requests; this.attachments = attachments; this.contentStore = contentStore; this.audit = audit; this.clock = clock;
    }

    public BirthCertificateRequestAttachment upload(AccountId actor, UUID requestId, AttachmentUpload upload) {
        CitizenId citizen = access.requireActiveCitizen(actor);
        BirthCertificateRequest request = ownedRequest(citizen, requestId);
        if (request.status() != BirthCertificateRequestStatus.SUBMITTED) throw new BirthCertificateRequestConflictException("Attachments can only be added before review starts");
        String contentType = AttachmentUploadPolicy.validateMetadata(upload);
        String storageKey = UUID.randomUUID().toString();
        Instant now = Instant.now(clock);
        BirthCertificateRequestAttachment attachment = new BirthCertificateRequestAttachment(UUID.randomUUID(), requestId, storageKey,
                AttachmentUploadPolicy.safeFileName(upload.originalFileName()), contentType, upload.sizeBytes(), actor, now);
        try (BufferedInputStream content = new BufferedInputStream(upload.content())) {
            AttachmentUploadPolicy.validateSignature(content, contentType);
            contentStore.store(storageKey, content);
        } catch (IOException exception) {
            throw new UncheckedIOException("Unable to read attachment", exception);
        }
        try {
            BirthCertificateRequestAttachment saved = attachments.save(attachment);
            audit.record(actor.value().toString(), "BIRTH_CERTIFICATE_REQUEST_ATTACHMENT_UPLOADED", "SERVICE_REQUEST", requestId.toString(), null, now);
            return saved;
        } catch (RuntimeException exception) {
            contentStore.delete(storageKey);
            throw exception;
        }
    }

    public List<BirthCertificateRequestAttachment> listMine(AccountId actor, UUID requestId) { return attachments.findByRequestId(ownedRequest(access.requireActiveCitizen(actor), requestId).id()); }
    public BirthCertificateAttachmentContent downloadMine(AccountId actor, UUID requestId, UUID attachmentId) { ownedRequest(access.requireActiveCitizen(actor), requestId); return content(requestId, attachmentId); }
    public List<BirthCertificateRequestAttachment> listForOperations(AccountId actor, UUID requestId) { access.requireActiveOperator(actor); requireRequest(requestId); return attachments.findByRequestId(requestId); }
    public BirthCertificateAttachmentContent downloadForOperations(AccountId actor, UUID requestId, UUID attachmentId) { access.requireActiveOperator(actor); requireRequest(requestId); return content(requestId, attachmentId); }

    private BirthCertificateRequest ownedRequest(CitizenId citizen, UUID requestId) { BirthCertificateRequest request = requireRequest(requestId); if (!request.belongsTo(citizen)) throw new NotFoundException("Request not found"); return request; }
    private BirthCertificateRequest requireRequest(UUID requestId) { return requests.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found")); }
    private BirthCertificateAttachmentContent content(UUID requestId, UUID attachmentId) { BirthCertificateRequestAttachment attachment = attachments.findByIdAndRequestId(attachmentId, requestId).orElseThrow(() -> new NotFoundException("Attachment not found")); return new BirthCertificateAttachmentContent(attachment, contentStore.read(attachment.storageKey())); }
}
