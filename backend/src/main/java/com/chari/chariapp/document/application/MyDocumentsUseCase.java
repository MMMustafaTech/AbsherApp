package com.chari.chariapp.document.application;

import com.chari.chariapp.account.domain.AccountId;
import com.chari.chariapp.document.domain.MyBirthCertificate;
import com.chari.chariapp.document.domain.MyNationalIdentity;
import com.chari.chariapp.document.domain.MyPassport;

public interface MyDocumentsUseCase {

    MyPassport getPassport(AccountId accountId);

    MyNationalIdentity getNationalIdentity(AccountId accountId);

    MyBirthCertificate getBirthCertificate(AccountId accountId);
}
