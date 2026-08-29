package com.absher.absherapp.document.application;

import com.absher.absherapp.account.domain.AccountId;
import com.absher.absherapp.document.domain.MyBirthCertificate;
import com.absher.absherapp.document.domain.MyNationalIdentity;
import com.absher.absherapp.document.domain.MyPassport;

public interface MyDocumentsUseCase {

    MyPassport getPassport(AccountId accountId);

    MyNationalIdentity getNationalIdentity(AccountId accountId);

    MyBirthCertificate getBirthCertificate(AccountId accountId);
}
