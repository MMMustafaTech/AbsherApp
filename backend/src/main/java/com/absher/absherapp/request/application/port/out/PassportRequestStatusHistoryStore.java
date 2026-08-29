package com.absher.absherapp.request.application.port.out;

import com.absher.absherapp.request.domain.PassportRequestStatusChange;

import java.util.List;
import java.util.UUID;

public interface PassportRequestStatusHistoryStore {
    void append(PassportRequestStatusChange change);
    List<PassportRequestStatusChange> findByRequestId(UUID requestId);
}
