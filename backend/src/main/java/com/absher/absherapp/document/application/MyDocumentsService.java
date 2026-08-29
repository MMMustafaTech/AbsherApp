package com.absher.absherapp.document.application;

import com.absher.absherapp.account.application.port.out.AccountStore;
import com.absher.absherapp.account.domain.Account;
import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.account.domain.AccountStatus;
import com.absher.absherapp.citizen.application.port.out.CitizenStore;
import com.absher.absherapp.citizen.domain.CitizenId;
import com.absher.absherapp.document.application.port.out.CitizenDocumentReadStore;
import com.absher.absherapp.document.domain.MyBirthCertificate;
import com.absher.absherapp.document.domain.MyNationalIdentity;
import com.absher.absherapp.document.domain.MyPassport;
import com.absher.absherapp.exception.NotFoundException;

import java.util.Objects;

/** Ownership is resolved from the JWT account, never from a request path parameter. */
public class MyDocumentsService implements MyDocumentsUseCase {

    private final AccountStore accountStore;
    private final CitizenStore citizenStore;
    private final CitizenDocumentReadStore documentStore;

    public MyDocumentsService(
            AccountStore accountStore,
            CitizenStore citizenStore,
            CitizenDocumentReadStore documentStore
    ) {
        this.accountStore = Objects.requireNonNull(accountStore, "Account store is required");
        this.citizenStore = Objects.requireNonNull(citizenStore, "Citizen store is required");
        this.documentStore = Objects.requireNonNull(documentStore, "Document store is required");
    }

    @Override
    public MyPassport getPassport(AccountId accountId) {
        return documentStore.findPassportByCitizenId(citizenIdFor(accountId))
                .orElseThrow(() -> new NotFoundException("Passport not found"));
    }

    @Override
    public MyNationalIdentity getNationalIdentity(AccountId accountId) {
        return documentStore.findNationalIdentityByCitizenId(citizenIdFor(accountId))
                .orElseThrow(() -> new NotFoundException("National identity not found"));
    }

    @Override
    public MyBirthCertificate getBirthCertificate(AccountId accountId) {
        return documentStore.findBirthCertificateByCitizenId(citizenIdFor(accountId))
                .orElseThrow(() -> new NotFoundException("Birth certificate not found"));
    }

    private CitizenId citizenIdFor(AccountId accountId) {
        Account account = accountStore.findById(accountId)
                .filter(candidate -> candidate.status() == AccountStatus.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Citizen account not found"));
        CitizenId citizenId = account.citizenIdOptional()
                .orElseThrow(() -> new NotFoundException("Citizen registry record not found"));
        citizenStore.findById(citizenId)
                .orElseThrow(() -> new NotFoundException("Citizen registry record not found"));
        return citizenId;
    }
}
