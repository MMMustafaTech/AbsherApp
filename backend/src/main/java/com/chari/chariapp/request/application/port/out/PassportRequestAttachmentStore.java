package com.chari.chariapp.request.application.port.out;

import com.chari.chariapp.request.domain.PassportRequestAttachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassportRequestAttachmentStore {
    PassportRequestAttachment save(PassportRequestAttachment attachment);

    List<PassportRequestAttachment> findByRequestId(UUID requestId);

    Optional<PassportRequestAttachment> findByIdAndRequestId(UUID attachmentId, UUID requestId);
}
