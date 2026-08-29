package com.absher.absherapp.account.application.port.out;

import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.citizen.domain.CitizenId;

import java.util.Optional;

public interface AccountStore {

    boolean existsByEmailLookup(String emailLookup);

    boolean existsByCitizenId(CitizenId citizenId);

    Optional<Account> findByEmailLookup(String emailLookup);

    Optional<Account> findById(AccountId accountId);

    default Account updateStatus(AccountId accountId, com.absher.absherapp.account.domain.AccountStatus status) {
        throw new UnsupportedOperationException("Account status updates are not supported");
    }

    /** Changes a password and invalidates every issued access token for the account. */
    default Account updatePasswordAndInvalidateAuthorization(AccountId accountId, String passwordHash) {
        throw new UnsupportedOperationException("Password updates are not supported");
    }

    /** Invalidates every issued access token without changing account details. */
    default Account invalidateAuthorization(AccountId accountId) {
        throw new UnsupportedOperationException("Authorization invalidation is not supported");
    }

    Account save(Account account);
}
