package com.chari.chariapp.request.application;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.exception.NotFoundException;
import com.chari.chariapp.request.application.port.out.PassportRequestStatusHistoryStore;
import com.chari.chariapp.request.application.port.out.PassportRequestStore;
import com.chari.chariapp.request.domain.PassportRequest;
import com.chari.chariapp.request.domain.PassportRequestStatus;
import com.chari.chariapp.request.domain.PassportRequestStatusChange;

import java.util.List;
import java.util.UUID;

/** Read-only use cases for citizen and operations passport-request views. */
public class PassportRequestQueryService {

    private final PassportRequestActorAccess actorAccess;
    private final PassportRequestStore requestStore;
    private final PassportRequestStatusHistoryStore historyStore;

    public PassportRequestQueryService(
            PassportRequestActorAccess actorAccess,
            PassportRequestStore requestStore,
            PassportRequestStatusHistoryStore historyStore
    ) {
        this.actorAccess = actorAccess;
        this.requestStore = requestStore;
        this.historyStore = historyStore;
    }

    public List<PassportRequest> mine(AccountId actorId) {
        CitizenId citizenId = actorAccess.requireActiveCitizen(actorId);
        return requestStore.findByCitizenId(citizenId);
    }

    public List<PassportRequestStatusChange> history(AccountId actorId, UUID requestId) {
        CitizenId citizenId = actorAccess.requireActiveCitizen(actorId);
        PassportRequest request = requestStore.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request not found"));

        if (!request.belongsTo(citizenId)) {
            // Avoid revealing whether a different citizen owns this request.
            throw new NotFoundException("Request not found");
        }
        return historyStore.findByRequestId(requestId);
    }

    public List<PassportRequest> byStatus(AccountId actorId, PassportRequestStatus status) {
        actorAccess.requireActiveOperator(actorId);
        return requestStore.findByStatus(status);
    }
}
