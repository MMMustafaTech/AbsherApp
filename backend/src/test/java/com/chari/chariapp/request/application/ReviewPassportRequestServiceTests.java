package com.chari.chariapp.request.application;

import com.chari.chariapp.account.application.port.out.AccountStore;
import com.chari.chariapp.account.domain.Account;
import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.account.domain.AccountRole;
import com.chari.chariapp.account.domain.AccountStatus;
import com.chari.chariapp.account.domain.EmailReference;
import com.chari.chariapp.citizen.domain.CitizenId;
import com.chari.chariapp.notification.application.NotificationService;
import com.chari.chariapp.request.application.port.out.PassportRequestStatusHistoryStore;
import com.chari.chariapp.request.application.port.out.PassportRequestStore;
import com.chari.chariapp.request.domain.PassportRequest;
import com.chari.chariapp.request.domain.PassportRequestStatusChange;
import com.chari.chariapp.shared.application.port.out.OperationalAuditStore;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReviewPassportRequestServiceTests {

    private static final Instant NOW = Instant.parse("2026-08-28T00:00:00Z");
    private static final Clock CLOCK = Clock.fixed(NOW, ZoneOffset.UTC);

    @Test
    void preventsAnEmployeeFromReviewingTheirOwnCitizenRequest() {
        CitizenId citizenId = CitizenId.newId();
        AccountId employeeId = AccountId.newId();
        PassportRequest request = PassportRequest.submitted(citizenId, NOW.minusSeconds(60));

        AccountStore accounts = mock(AccountStore.class);
        PassportRequestStore requests = mock(PassportRequestStore.class);
        PassportRequestStatusHistoryStore history = mock(PassportRequestStatusHistoryStore.class);
        OperationalAuditStore audit = mock(OperationalAuditStore.class);
        when(accounts.findById(employeeId)).thenReturn(Optional.of(employee(employeeId, citizenId)));
        when(requests.findByIdForUpdate(request.id())).thenReturn(Optional.of(request));

        ReviewPassportRequestService service = new ReviewPassportRequestService(
                new PassportRequestActorAccess(accounts), requests, history, audit,
                mock(NotificationService.class), CLOCK
        );

        assertThatThrownBy(() -> service.startReview(employeeId, request.id()))
                .isInstanceOf(PassportRequestConflictException.class)
                .hasMessage("An operator cannot review their own passport request");

        verify(requests, never()).save(any());
        verify(history, never()).append(any());
        verify(audit, never()).record(any(), any(), any(), any(), any(), any());
    }

    @Test
    void recordsAnImmutableHistoryEntryWhenAReviewStarts() {
        CitizenId citizenId = CitizenId.newId();
        AccountId employeeId = AccountId.newId();
        PassportRequest request = PassportRequest.submitted(citizenId, NOW.minusSeconds(60));

        AccountStore accounts = mock(AccountStore.class);
        PassportRequestStore requests = mock(PassportRequestStore.class);
        PassportRequestStatusHistoryStore history = mock(PassportRequestStatusHistoryStore.class);
        OperationalAuditStore audit = mock(OperationalAuditStore.class);
        when(accounts.findById(employeeId)).thenReturn(Optional.of(employee(employeeId, null)));
        when(requests.findByIdForUpdate(request.id())).thenReturn(Optional.of(request));
        when(requests.save(any(PassportRequest.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewPassportRequestService service = new ReviewPassportRequestService(
                new PassportRequestActorAccess(accounts), requests, history, audit,
                mock(NotificationService.class), CLOCK
        );

        PassportRequest updated = service.startReview(employeeId, request.id());

        assertThat(updated.reviewedBy()).isEqualTo(employeeId);
        ArgumentCaptor<PassportRequestStatusChange> historyChange = ArgumentCaptor.forClass(PassportRequestStatusChange.class);
        verify(history).append(historyChange.capture());
        assertThat(historyChange.getValue())
                .extracting(
                        PassportRequestStatusChange::requestId,
                        PassportRequestStatusChange::fromStatus,
                        PassportRequestStatusChange::toStatus,
                        PassportRequestStatusChange::changedBy,
                        PassportRequestStatusChange::changedAt
                )
                .containsExactly(request.id(), request.status(), updated.status(), employeeId, NOW);
    }

    private Account employee(AccountId accountId, CitizenId citizenId) {
        return new Account(
                accountId,
                citizenId,
                new EmailReference("a".repeat(64), "employee-ciphertext"),
                "bcrypt-hash",
                AccountStatus.ACTIVE,
                java.util.Set.of(AccountRole.EMPLOYEE),
                NOW
        );
    }
}
