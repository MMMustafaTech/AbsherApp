package com.absher.absherapp.request.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.request.application.port.out.PassportRequestStatusHistoryStore;
import com.absher.absherapp.request.application.port.out.PassportRequestStore;
import com.absher.absherapp.request.domain.PassportRequest;
import com.absher.absherapp.request.domain.PassportRequestStatus;
import com.absher.absherapp.request.domain.PassportRequestStatusChange;

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
