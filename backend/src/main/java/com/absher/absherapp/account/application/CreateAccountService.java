package com.absher.absherapp.account.application;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.application.port.out.PasswordHasher;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.account.domain.EmailReference;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;

public class CreateAccountService implements CreateAccountUseCase {

    private final AccountStore accountStore;
    private final PasswordHasher passwordHasher;
    private final Clock clock;

    public CreateAccountService(AccountStore accountStore, PasswordHasher passwordHasher, Clock clock) {
        this.accountStore = Objects.requireNonNull(accountStore, "Account store is required");
        this.passwordHasher = Objects.requireNonNull(passwordHasher, "Password hasher is required");
        this.clock = Objects.requireNonNull(clock, "Clock is required");
    }

    @Override
    public AccountId create(CreateAccountCommand command) {
        Objects.requireNonNull(command, "Create account command is required");
        validatePassword(command.rawPassword());

        EmailReference email = new EmailReference(command.emailLookup(), command.encryptedEmail());
        if (accountStore.existsByEmailLookup(email.lookup())) {
            throw new AccountAlreadyExistsException("email");
        }
        if (command.citizenId() != null && accountStore.existsByCitizenId(command.citizenId())) {
            throw new AccountAlreadyExistsException("citizen");
        }

        Account account = new Account(
                AccountId.newId(),
                command.citizenId(),
                email,
                passwordHasher.hash(command.rawPassword()),
                AccountStatus.PENDING_VERIFICATION,
                command.roles(),
                Instant.now(clock)
        );
        return accountStore.save(account).id();
    }

    private void validatePassword(String password) {
        if (password == null || password.length() < 12 || password.length() > 128) {
            throw new WeakPasswordException();
        }
    }
}
