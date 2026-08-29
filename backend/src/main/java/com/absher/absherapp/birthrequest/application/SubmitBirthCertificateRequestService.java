package com.absher.absherapp.birthrequest.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.birthrequest.application.port.out.*;
import com.absher.absherapp.birthrequest.domain.*;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.UUID;

public class SubmitBirthCertificateRequestService {
    private final PassportRequestActorAccess access; private final BirthCertificateRequestStore requests; private final BirthCertificateRequestStatusHistoryStore history; private final OperationalAuditStore audit; private final Clock clock;
    private final NewbornRegistrationDetailsStore newbornDetails;
    public SubmitBirthCertificateRequestService(PassportRequestActorAccess access, BirthCertificateRequestStore requests, BirthCertificateRequestStatusHistoryStore history, NewbornRegistrationDetailsStore newbornDetails, OperationalAuditStore audit, Clock clock) { this.access = access; this.requests = requests; this.history = history; this.newbornDetails = newbornDetails; this.audit = audit; this.clock = clock; }
    @Transactional public BirthCertificateRequest submit(AccountId actorId, BirthCertificateRequestKind kind, String reason, NewbornRegistrationDetails details) {
        if (kind == BirthCertificateRequestKind.NEWBORN_REGISTRATION && details == null) throw new IllegalArgumentException("Newborn registration details are required");
        if (kind != BirthCertificateRequestKind.NEWBORN_REGISTRATION && details != null) throw new IllegalArgumentException("Newborn details are only valid for a newborn registration");
        CitizenId citizenId = access.requireActiveCitizen(actorId);
        if (requests.hasOpenRequest(citizenId)) throw new BirthCertificateRequestConflictException("An open birth certificate request already exists");
        Instant now = Instant.now(clock); BirthCertificateRequest request = BirthCertificateRequest.submitted(citizenId, kind, reason, now);
        try { requests.save(request); } catch (DataIntegrityViolationException ex) { throw new BirthCertificateRequestConflictException("An open birth certificate request already exists"); }
        if (details != null) newbornDetails.save(request.id(), details);
        history.append(new BirthCertificateRequestStatusChange(UUID.randomUUID(), request.id(), null, BirthCertificateRequestStatus.SUBMITTED, null, actorId, now));
        audit.record(actorId.value().toString(), "BIRTH_CERTIFICATE_REQUEST_SUBMITTED", "SERVICE_REQUEST", request.id().toString(), "kind=" + kind, now);
        return request;
    }
}
