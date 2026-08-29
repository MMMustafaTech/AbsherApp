package com.chari.chariapp.request.application.port.out;

import com.chari.chariapp.request.domain.PassportRequestStatusChange;

import java.util.List;
import java.util.UUID;

public interface PassportRequestStatusHistoryStore {
    void append(PassportRequestStatusChange change);
    List<PassportRequestStatusChange> findByRequestId(UUID requestId);
}
