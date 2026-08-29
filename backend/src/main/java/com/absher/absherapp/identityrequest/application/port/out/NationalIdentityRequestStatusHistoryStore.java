package com.absher.absherapp.identityrequest.application.port.out;

import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatusChange;

import java.util.List;
import java.util.UUID;

public interface NationalIdentityRequestStatusHistoryStore {
    void append(NationalIdentityRequestStatusChange change);
    List<NationalIdentityRequestStatusChange> findByRequestId(UUID requestId);
}
