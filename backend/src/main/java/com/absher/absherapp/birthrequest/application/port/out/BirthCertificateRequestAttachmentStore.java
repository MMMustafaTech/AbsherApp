package com.absher.absherapp.birthrequest.application.port.out;

import com.absher.absherapp.birthrequest.domain.BirthCertificateRequestAttachment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BirthCertificateRequestAttachmentStore {
    BirthCertificateRequestAttachment save(BirthCertificateRequestAttachment attachment);
    List<BirthCertificateRequestAttachment> findByRequestId(UUID requestId);
    Optional<BirthCertificateRequestAttachment> findByIdAndRequestId(UUID attachmentId, UUID requestId);
}
