package com.absher.absherapp.identityrequest.application.port.out;

import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestAttachment;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NationalIdentityRequestAttachmentStore {
    NationalIdentityRequestAttachment save(NationalIdentityRequestAttachment attachment);
    List<NationalIdentityRequestAttachment> findByRequestId(UUID requestId);
    Optional<NationalIdentityRequestAttachment> findByIdAndRequestId(UUID attachmentId, UUID requestId);
}
