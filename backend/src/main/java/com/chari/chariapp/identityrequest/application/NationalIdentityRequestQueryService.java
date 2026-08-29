package com.chari.chariapp.identityrequest.application;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.exception.NotFoundException;
import com.chari.chariapp.identityrequest.application.port.out.NationalIdentityRequestStatusHistoryStore;
import com.chari.chariapp.identityrequest.application.port.out.NationalIdentityRequestStore;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequest;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestStatus;
import com.chari.chariapp.identityrequest.domain.NationalIdentityRequestStatusChange;
import com.chari.chariapp.request.application.PassportRequestActorAccess;

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
