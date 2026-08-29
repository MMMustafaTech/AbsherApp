package com.chari.chariapp.request.application;

import com.chari.chariapp.account.application.port.out.AccountStore;
import com.chari.chariapp.account.domain.Account;
import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.account.domain.AccountRole;
import com.chari.chariapp.account.domain.AccountStatus;
import com.chari.chariapp.citizen.domain.CitizenId;
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
