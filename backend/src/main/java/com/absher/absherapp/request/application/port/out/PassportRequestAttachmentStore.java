package com.absher.absherapp.request.application.port.out;

import com.absher.absherapp.request.domain.PassportRequestAttachment;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PassportRequestAttachmentStore {
    PassportRequestAttachment save(PassportRequestAttachment attachment);

    List<PassportRequestAttachment> findByRequestId(UUID requestId);

    Optional<PassportRequestAttachment> findByIdAndRequestId(UUID attachmentId, UUID requestId);
}
