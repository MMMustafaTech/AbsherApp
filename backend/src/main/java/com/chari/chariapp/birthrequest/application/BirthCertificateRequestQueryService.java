package com.chari.chariapp.birthrequest.application;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.birthrequest.application.port.out.*;
import com.chari.chariapp.birthrequest.domain.*;
import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.exception.NotFoundException;
import com.chari.chariapp.request.application.PassportRequestActorAccess;
import java.util.*;

public class BirthCertificateRequestQueryService {
    private final PassportRequestActorAccess access; private final BirthCertificateRequestStore requests; private final BirthCertificateRequestStatusHistoryStore history; private final NewbornRegistrationDetailsStore newbornDetails;
    public BirthCertificateRequestQueryService(PassportRequestActorAccess access, BirthCertificateRequestStore requests, BirthCertificateRequestStatusHistoryStore history, NewbornRegistrationDetailsStore newbornDetails) { this.access = access; this.requests = requests; this.history = history; this.newbornDetails = newbornDetails; }
    public List<BirthCertificateRequest> mine(AccountId actor) { return requests.findByCitizenId(access.requireActiveCitizen(actor)); }
    public List<BirthCertificateRequestStatusChange> history(AccountId actor, UUID requestId) { CitizenId citizen = access.requireActiveCitizen(actor); BirthCertificateRequest request = requests.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found")); if (!request.belongsTo(citizen)) throw new NotFoundException("Request not found"); return history.findByRequestId(requestId); }
    public List<BirthCertificateRequest> byStatus(AccountId actor, BirthCertificateRequestStatus status) { access.requireActiveOperator(actor); return requests.findByStatus(status); }
    public NewbornRegistrationDetails myNewbornRegistration(AccountId actor, UUID requestId) { CitizenId citizen = access.requireActiveCitizen(actor); BirthCertificateRequest request = requireNewbornRegistration(requestId); if (!request.belongsTo(citizen)) throw new NotFoundException("Request not found"); return newbornDetails.findByRequestId(requestId).orElseThrow(() -> new NotFoundException("Newborn registration details not found")); }
    public NewbornRegistrationDetails operationalNewbornRegistration(AccountId actor, UUID requestId) { access.requireActiveOperator(actor); requireNewbornRegistration(requestId); return newbornDetails.findByRequestId(requestId).orElseThrow(() -> new NotFoundException("Newborn registration details not found")); }
    private BirthCertificateRequest requireNewbornRegistration(UUID requestId) { BirthCertificateRequest request = requests.findById(requestId).orElseThrow(() -> new NotFoundException("Request not found")); if (request.kind() != BirthCertificateRequestKind.NEWBORN_REGISTRATION) throw new NotFoundException("Newborn registration details not found"); return request; }
}
