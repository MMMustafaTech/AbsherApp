package com.absher.absherapp.account.application;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.application.port.out.PasswordHasher;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.account.domain.EmailReference;
import com.absher.absherapp.shared.application.port.out.OperationalAuditStore;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/** Administrative boundary: an admin may provision and disable EMPLOYEE accounts, never grant ADMIN. */
public class EmployeeAccountAdministrationService implements EmployeeAccountAdministrationUseCase {
    private final AccountStore accountStore;
    private final PasswordHasher passwordHasher;
    private final OperationalAuditStore auditStore;
    private final Clock clock;

    public EmployeeAccountAdministrationService(
            AccountStore accountStore, PasswordHasher passwordHasher, OperationalAuditStore auditStore, Clock clock
    ) {
        this.accountStore = Objects.requireNonNull(accountStore, "Account store is required");
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "Password hasher is required");
        this.auditStore = Objects.requireNonNull(auditStore, "Audit store is required");
        this.clock = Objects.requireNonNull(clock, "Clock is required");
    }

    @Override
    @Transactional
    public AccountId provisionEmployee(ProvisionEmployeeAccountCommand command) {
        Objects.requireNonNull(command, "Employee provisioning command is required");
        requireActiveAdministrator(command.administratorId());
        if (command.rawPassword() == null || command.rawPassword().length() < 12 || command.rawPassword().length() > 128) {
            throw new WeakPasswordException();
        }
        EmailReference email = new EmailReference(command.emailLookup(), command.encryptedEmail());
        if (accountStore.existsByEmailLookup(email.lookup())) {
            throw new AccountAlreadyExistsException("email");
        }
        Instant now = Instant.now(clock);
        Account employee = new Account(AccountId.newId(), null, email, passwordHasher.hash(command.rawPassword()),
                AccountStatus.ACTIVE, Set.of(AccountRole.EMPLOYEE), now);
        Account saved = accountStore.save(employee);
        auditStore.record(command.administratorId().value().toString(), "EMPLOYEE_ACCOUNT_PROVISIONED", "ACCOUNT",
                saved.id().value().toString(), null, now);
        return saved.id();
    }

    @Override
    @Transactional
    public void changeEmployeeStatus(ChangeEmployeeAccountStatusCommand command) {
        Objects.requireNonNull(command, "Employee status command is required");
        requireActiveAdministrator(command.administratorId());
        if (command.administratorId().equals(command.employeeId())
                || (command.status() != AccountStatus.ACTIVE && command.status() != AccountStatus.DISABLED)) {
            throw new IllegalArgumentException("Invalid employee status change");
        }
        Account employee = accountStore.findById(command.employeeId()).orElseThrow(() -> new IllegalArgumentException("Account not found"));
        if (!employee.roles().equals(Set.of(AccountRole.EMPLOYEE))) {
            throw new IllegalArgumentException("Only employee accounts can be managed here");
        }
        accountStore.updateStatus(command.employeeId(), command.status());
        auditStore.record(command.administratorId().value().toString(), "EMPLOYEE_ACCOUNT_STATUS_CHANGED", "ACCOUNT",
                command.employeeId().value().toString(), "{\"status\":\"" + command.status() + "\"}", Instant.now(clock));
    }

    private void requireActiveAdministrator(AccountId accountId) {
        Account administrator = accountStore.findById(accountId).orElseThrow(() -> new IllegalArgumentException("Administrator unavailable"));
        if (administrator.status() != AccountStatus.ACTIVE || !administrator.roles().contains(AccountRole.ADMIN)) {
            throw new IllegalArgumentException("Administrator unavailable");
        }
    }
}
