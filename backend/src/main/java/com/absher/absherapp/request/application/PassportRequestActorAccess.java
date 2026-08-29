package com.absher.absherapp.request.application;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountRole;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.citizen.domain.CitizenId;
import org.springframework.security.access.AccessDeniedException;

/**
 * Resolves the current actor from the authoritative account store instead of
 * trusting roles or citizen identifiers supplied by the client.
 */
public class PassportRequestActorAccess {

    private final AccountStore accountStore;

    public PassportRequestActorAccess(AccountStore accountStore) {
        this.accountStore = accountStore;
    }

    public CitizenId requireActiveCitizen(AccountId accountId) {
        return requireActiveAccount(accountId)
                .filter(account -> account.roles().contains(AccountRole.CITIZEN))
                .flatMap(Account::citizenIdOptional)
                .orElseThrow(() -> new AccessDeniedException("Citizen access is required"));
    }

    public Account requireActiveOperator(AccountId accountId) {
        Account account = requireActiveAccount(accountId)
                .orElseThrow(() -> new AccessDeniedException("Operator access is required"));

        if (!account.roles().contains(AccountRole.EMPLOYEE)
                && !account.roles().contains(AccountRole.ADMIN)) {
            throw new AccessDeniedException("Operator access is required");
        }
        return account;
    }

    private java.util.Optional<Account> requireActiveAccount(AccountId accountId) {
        return accountStore.findById(accountId)
                .filter(account -> account.status() == AccountStatus.ACTIVE);
    }
}
