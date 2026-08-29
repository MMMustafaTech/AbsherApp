package com.chari.chariapp.account.application;

import com.chari.chariapp.account.application.port.out.AccountStore;
import com.chari.chariapp.account.application.port.out.PasswordHasher;
import com.chari.chariapp.account.application.port.out.RefreshSessionStore;
import com.chari.chariapp.account.domain.Account;
import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.account.domain.AccountRole;
import com.chari.chariapp.account.domain.AccountStatus;
import com.chari.chariapp.shared.application.port.out.OperationalAuditStore;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

/** Security actions that apply only to the authenticated citizen's own account. */
public class CitizenSecurityService {

    private final AccountStore accountStore;
    private final PasswordHasher passwordHasher;
    private final RefreshSessionStore refreshSessions;
    private final OperationalAuditStore auditStore;
    private final Clock clock;

    public CitizenSecurityService(
            AccountStore accountStore,
            PasswordHasher passwordHasher,
            RefreshSessionStore refreshSessions,
            OperationalAuditStore auditStore,
            Clock clock
    ) {
        this.accountStore = Objects.requireNonNull(accountStore, "Account store is required");
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "Password hasher is required");
        this.refreshSessions = Objects.requireNonNull(refreshSessions, "Refresh session store is required");
        this.auditStore = Objects.requireNonNull(auditStore, "Audit store is required");
        this.clock = Objects.requireNonNull(clock, "Clock is required");
    }

    @Transactional
    public void changePassword(AccountId accountId, String currentPassword, String newPassword) {
        Account account = requireActiveCitizen(accountId);
        if (!passwordHasher.matches(currentPassword, account.passwordHash())) {
            throw new InvalidCredentialsException();
        }
        if (newPassword == null || newPassword.length() < 12 || newPassword.length() > 128
                || passwordHasher.matches(newPassword, account.passwordHash())) {
            throw new WeakPasswordException();
        }

        Instant now = Instant.now(clock);
        accountStore.updatePasswordAndInvalidateAuthorization(account.id(), passwordHasher.hash(newPassword));
        refreshSessions.revokeAllForAccount(account.id(), now);
        auditStore.record(account.id().value().toString(), "CITIZEN_PASSWORD_CHANGED", "ACCOUNT",
                account.id().value().toString(), null, now);
    }

    @Transactional
    public int logoutAllDevices(AccountId accountId) {
        Account account = requireActiveCitizen(accountId);
        Instant now = Instant.now(clock);
        accountStore.invalidateAuthorization(account.id());
        int revokedSessions = refreshSessions.revokeAllForAccount(account.id(), now);
        auditStore.record(account.id().value().toString(), "CITIZEN_LOGGED_OUT_ALL_DEVICES", "ACCOUNT",
                account.id().value().toString(), null, now);
        return revokedSessions;
    }

    private Account requireActiveCitizen(AccountId accountId) {
        return accountStore.findById(accountId)
                .filter(account -> account.status() == AccountStatus.ACTIVE)
                .filter(account -> account.roles().contains(AccountRole.CITIZEN))
                .filter(account -> account.citizenIdOptional().isPresent())
                .orElseThrow(() -> new AccessDeniedException("Citizen access is required"));
    }
}
