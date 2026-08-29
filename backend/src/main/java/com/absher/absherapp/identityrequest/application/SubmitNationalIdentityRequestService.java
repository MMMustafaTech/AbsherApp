package com.absher.absherapp.identityrequest.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStatusHistoryStore;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStore;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequest;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestKind;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatus;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatusChange;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class SubmitNationalIdentityRequestService {
    private final PassportRequestActorAccess actorAccess;
    private final NationalIdentityRequestStore requestStore;
    private final NationalIdentityRequestStatusHistoryStore historyStore;
    private final OperationalAuditStore auditStore;
    private final Clock clock;

    public SubmitNationalIdentityRequestService(PassportRequestActorAccess actorAccess, NationalIdentityRequestStore requestStore,
                                                NationalIdentityRequestStatusHistoryStore historyStore, OperationalAuditStore auditStore,
                                                Clock clock) {
        this.actorAccess = actorAccess;
        this.requestStore = requestStore;
        this.historyStore = historyStore;
        this.auditStore = auditStore;
        this.clock = clock;
    }

    @Transactional
    public NationalIdentityRequest submit(AccountId actorId, NationalIdentityRequestKind kind, String reason) {
        CitizenId citizenId = actorAccess.requireActiveCitizen(actorId);
        if (requestStore.hasOpenRequest(citizenId)) {
            throw new NationalIdentityRequestConflictException("An open national identity request already exists");
        }
        Instant now = Instant.now(clock);
        NationalIdentityRequest request = NationalIdentityRequest.submitted(citizenId, kind, reason, now);
        try {
            requestStore.save(request);
        } catch (DataIntegrityViolationException exception) {
            throw new NationalIdentityRequestConflictException("An open national identity request already exists");
        }
        historyStore.append(new NationalIdentityRequestStatusChange(UUID.randomUUID(), request.id(), null,
                NationalIdentityRequestStatus.SUBMITTED, null, actorId, now));
        auditStore.record(actorId.value().toString(), "NATIONAL_IDENTITY_REQUEST_SUBMITTED", "SERVICE_REQUEST",
                request.id().toString(), "kind=" + kind, now);
        return request;
    }
}
