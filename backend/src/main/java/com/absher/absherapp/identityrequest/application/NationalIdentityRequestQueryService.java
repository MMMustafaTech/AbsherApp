package com.absher.absherapp.identityrequest.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStatusHistoryStore;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStore;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequest;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatus;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatusChange;
import com.absher.absherapp.request.application.PassportRequestActorAccess;

import java.util.List;
import java.util.UUID;

public class NationalIdentityRequestQueryService {
    private final PassportRequestActorAccess actorAccess;
    private final NationalIdentityRequestStore requestStore;
    private final NationalIdentityRequestStatusHistoryStore historyStore;

    public NationalIdentityRequestQueryService(PassportRequestActorAccess actorAccess, NationalIdentityRequestStore requestStore,
                                               NationalIdentityRequestStatusHistoryStore historyStore) {
        this.actorAccess = actorAccess;
        this.requestStore = requestStore;
        this.historyStore = historyStore;
    }

    public List<NationalIdentityRequest> mine(AccountId actorId) {
        return requestStore.findByCitizenId(actorAccess.requireActiveCitizen(actorId));
    }

    public List<NationalIdentityRequestStatusChange> history(AccountId actorId, UUID requestId) {
        CitizenId citizenId = actorAccess.requireActiveCitizen(actorId);
        NationalIdentityRequest request = requestStore.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found"));
        if (!request.belongsTo(citizenId)) {
            throw new NotFoundException("Request not found");
        }
        return historyStore.findByRequestId(requestId);
    }

    public List<NationalIdentityRequest> byStatus(AccountId actorId, NationalIdentityRequestStatus status) {
        actorAccess.requireActiveOperator(actorId);
        return requestStore.findByStatus(status);
    }
}
