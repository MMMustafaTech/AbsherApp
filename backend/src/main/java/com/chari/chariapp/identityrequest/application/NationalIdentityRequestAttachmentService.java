package com.chari.chariapp.identityrequest.application;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.exception.NotFoundException;
import com.chari.chariapp.identityrequest.application.port.out.NationalIdentityRequestAttachmentStore;
import com.chari.chariapp.identityrequest.application.port.out.NationalIdentityRequestStore;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequest;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestAttachment;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestStatus;
import com.chari.chariapp.request.application.AttachmentUpload;
import com.chari.chariapp.request.application.AttachmentUploadPolicy;
import com.chari.chariapp.request.application.PassportRequestActorAccess;
import com.chari.chariapp.request.application.port.out.AttachmentContentStore;
import com.chari.chariapp.shared.application.port.out.OperationalAuditStore;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/** Owns authorization and validated file storage for national-identity request attachments. */
public class NationalIdentityRequestAttachmentService {
    private final PassportRequestActorAccess access; private final NationalIdentityRequestStore requests;
    private final NationalIdentityRequestAttachmentStore attachments; private final AttachmentContentStore contentStore;
    private final OperationalAuditStore audit; private final Clock clock;
    public NationalIdentityRequestAttachmentService(PassportRequestActorAccess access, NationalIdentityRequestStore requests,
                                                    NationalIdentityRequestAttachmentStore attachments, AttachmentContentStore contentStore,
                                                    OperationalAuditStore audit, Clock clock) {
        this.access=access; this.requests=requests; this.attachments=attachments; this.contentStore=contentStore; this.audit=audit; this.clock=clock;
    }
    public NationalIdentityRequestAttachment upload(AccountId actor, UUID requestId, AttachmentUpload upload) {
        CitizenId citizen=access.requireActiveCitizen(actor); NationalIdentityRequest request=ownedRequest(citizen, requestId);
        if (request.status()!=NationalIdentityRequestStatus.SUBMITTED) throw new NationalIdentityRequestConflictException("Attachments can only be added before review starts");
        String contentType=AttachmentUploadPolicy.validateMetadata(upload); String storageKey=UUID.randomUUID().toString(); Instant now=Instant.now(clock);
        NationalIdentityRequestAttachment attachment=new NationalIdentityRequestAttachment(UUID.randomUUID(),requestId,storageKey,
                AttachmentUploadPolicy.safeFileName(upload.originalFileName()),contentType,upload.sizeBytes(),actor,now);
        try (BufferedInputStream content=new BufferedInputStream(upload.content())) { AttachmentUploadPolicy.validateSignature(content,contentType); contentStore.store(storageKey,content); }
        catch (IOException exception) { throw new UncheckedIOException("Unable to read attachment",exception); }
        try { NationalIdentityRequestAttachment saved=attachments.save(attachment); audit.record(actor.value().toString(),"NATIONAL_IDENTITY_REQUEST_ATTACHMENT_UPLOADED","SERVICE_REQUEST",requestId.toString(),null,now); return saved; }
        catch (RuntimeException exception) { contentStore.delete(storageKey); throw exception; }
    }
    public List<NationalIdentityRequestAttachment> listMine(AccountId actor, UUID requestId) { return attachments.findByRequestId(ownedRequest(access.requireActiveCitizen(actor),requestId).id()); }
    public NationalIdentityAttachmentContent downloadMine(AccountId actor, UUID requestId, UUID attachmentId) { ownedRequest(access.requireActiveCitizen(actor),requestId); return content(requestId,attachmentId); }
    public List<NationalIdentityRequestAttachment> listForOperations(AccountId actor, UUID requestId) { access.requireActiveOperator(actor); requireRequest(requestId); return attachments.findByRequestId(requestId); }
    public NationalIdentityAttachmentContent downloadForOperations(AccountId actor, UUID requestId, UUID attachmentId) { access.requireActiveOperator(actor); requireRequest(requestId); return content(requestId,attachmentId); }
    private NationalIdentityRequest ownedRequest(CitizenId citizen, UUID requestId) { NationalIdentityRequest request=requireRequest(requestId); if(!request.belongsTo(citizen)) throw new NotFoundException("Request not found"); return request; }
    private NationalIdentityRequest requireRequest(UUID requestId) { return requests.findById(requestId).orElseThrow(()->new NotFoundException("Request not found")); }
    private NationalIdentityAttachmentContent content(UUID requestId, UUID attachmentId) { NationalIdentityRequestAttachment attachment=attachments.findByIdAndRequestId(attachmentId,requestId).orElseThrow(()->new NotFoundException("Attachment not found")); return new NationalIdentityAttachmentContent(attachment,contentStore.read(attachment.storageKey())); }
}
