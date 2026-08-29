package com.absher.absherapp.identityrequest.application;

import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.exception.NotFoundException;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStatusHistoryStore;
import com.absher.absherapp.identityrequest.application.port.out.NationalIdentityRequestStore;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequest;
import com.absher.absherapp.identityrequest.domain.NationalIdentityRequestStatusChange;
import com.absher.absherapp.request.application.PassportRequestActorAccess;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import com.absher.absherapp.notification.application.NotificationService;
import com.absher.absherapp.notification.domain.NotificationType;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.UUID;

public class ReviewNationalIdentityRequestService {
    private final PassportRequestActorAccess actorAccess;
    private final NationalIdentityRequestStore requestStore;
    private final NationalIdentityRequestStatusHistoryStore historyStore;
    private final OperationalAuditStore auditStore;
    private final NotificationService notifications;
    private final Clock clock;

    public ReviewNationalIdentityRequestService(PassportRequestActorAccess actorAccess, NationalIdentityRequestStore requestStore,
                                                NationalIdentityRequestStatusHistoryStore historyStore, OperationalAuditStore auditStore, NotificationService notifications,
                                                Clock clock) {
        this.actorAccess = actorAccess;
        this.requestStore = requestStore;
        this.historyStore = historyStore;
        this.auditStore = auditStore;
        this.notifications = notifications;
        this.clock = clock;
    }

    @Transactional
    public NationalIdentityRequest startReview(AccountId actorId, UUID requestId) {
        Account operator = actorAccess.requireActiveOperator(actorId);
        NationalIdentityRequest current = findForUpdate(requestId);
        rejectSelfReview(operator, current);
        Instant now = Instant.now(clock);
        NationalIdentityRequest updated = current.startReview(actorId, now);
        requestStore.save(updated);
        appendHistory(updated, current, null, actorId, now);
        auditStore.record(actorId.value().toString(), "NATIONAL_IDENTITY_REQUEST_REVIEW_STARTED", "SERVICE_REQUEST",
                requestId.toString(), null, now);
        return updated;
    }

    @Transactional
    public NationalIdentityRequest decide(AccountId actorId, UUID requestId, boolean approved, String reason) {
        Account operator = actorAccess.requireActiveOperator(actorId);
        NationalIdentityRequest current = findForUpdate(requestId);
        rejectSelfReview(operator, current);
        Instant now = Instant.now(clock);
        NationalIdentityRequest updated = current.decide(actorId, approved, reason, now);
        requestStore.save(updated);
        appendHistory(updated, current, updated.decisionReason(), actorId, now);
        auditStore.record(actorId.value().toString(), approved ? "NATIONAL_IDENTITY_REQUEST_APPROVED" : "NATIONAL_IDENTITY_REQUEST_REJECTED",
                "SERVICE_REQUEST", requestId.toString(), null, now);
        notifications.publish(updated.citizenId(), approved ? NotificationType.NATIONAL_IDENTITY_REQUEST_APPROVED : NotificationType.NATIONAL_IDENTITY_REQUEST_REJECTED,
                approved ? "National identity request approved" : "National identity request rejected",
                approved ? "Your national identity request has been approved." : "Your national identity request has been rejected.");
        return updated;
    }

    private NationalIdentityRequest findForUpdate(UUID requestId) {
        return requestStore.findByIdForUpdate(requestId).orElseThrow(() -> new NotFoundException("Request not found"));
    }

    private void rejectSelfReview(Account operator, NationalIdentityRequest request) {
        if (operator.citizenIdOptional().filter(request.citizenId()::equals).isPresent()) {
            throw new NationalIdentityRequestConflictException("An operator cannot review their own national identity request");
        }
    }

    private void appendHistory(NationalIdentityRequest updated, NationalIdentityRequest previous, String reason,
                               AccountId actorId, Instant occurredAt) {
        historyStore.append(new NationalIdentityRequestStatusChange(UUID.randomUUID(), updated.id(), previous.status(),
                updated.status(), reason, actorId, occurredAt));
    }
}
