package com.chari.chariapp.request.application;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.request.application.port.out.PassportRequestStatusHistoryStore;
import com.chari.chariapp.request.application.port.out.PassportRequestStore;
import com.chari.chariapp.request.domain.PassportRequest;
import com.chari.chariapp.request.domain.PassportRequestStatus;
import com.chari.chariapp.request.domain.PassportRequestKind;
import com.chari.chariapp.request.domain.PassportRequestStatusChange;
import com.chari.chariapp.shared.application.port.out.OperationalAuditStore;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

/** Handles the citizen command that creates a new passport request. */
public class SubmitPassportRequestService {

    private final PassportRequestActorAccess actorAccess;
    private final PassportRequestStore requestStore;
    private final PassportRequestStatusHistoryStore historyStore;
    private final OperationalAuditStore auditStore;
    private final Clock clock;

    public SubmitPassportRequestService(
            PassportRequestActorAccess actorAccess,
            PassportRequestStore requestStore,
            PassportRequestStatusHistoryStore historyStore,
            OperationalAuditStore auditStore,
            Clock clock
    ) {
        this.actorAccess = actorAccess;
        this.requestStore = requestStore;
        this.historyStore = historyStore;
        this.auditStore = auditStore;
        this.clock = clock;
    }

    @Transactional
    public PassportRequest submit(AccountId actorId) {
        return submit(actorId, PassportRequestKind.ISSUANCE, null);
    }

    @Transactional
    public PassportRequest submit(AccountId actorId, PassportRequestKind kind, String requestReason) {
        CitizenId citizenId = actorAccess.requireActiveCitizen(actorId);
        if (requestStore.hasOpenRequest(citizenId)) {
            throw new PassportRequestConflictException("An open passport request already exists");
        }

        Instant submittedAt = Instant.now(clock);
        PassportRequest request = PassportRequest.submitted(citizenId, kind, requestReason, submittedAt);

        try {
            requestStore.save(request);
        } catch (DataIntegrityViolationException exception) {
            // A database-level uniqueness rule can be added later without leaking its details to callers.
            throw new PassportRequestConflictException("An open passport request already exists");
        }

        historyStore.append(new PassportRequestStatusChange(
                UUID.randomUUID(), request.id(), null, PassportRequestStatus.SUBMITTED,
                null, actorId, submittedAt
        ));
        auditStore.record(
                actorId.value().toString(), "PASSPORT_REQUEST_SUBMITTED", "SERVICE_REQUEST",
                request.id().toString(), "kind=" + request.kind(), submittedAt
        );
        return request;
    }
}
