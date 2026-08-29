package com.chari.chariapp.identityrequest.application.port.out;

import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestStatusChange;

import java.util.List;
import java.util.UUID;

public interface NationalIdentityRequestStatusHistoryStore {
    void append(NationalIdentityRequestStatusChange change);
    List<NationalIdentityRequestStatusChange> findByRequestId(UUID requestId);
}
